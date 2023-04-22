package ru.practicum.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.event.*;
import ru.practicum.dto.request.RequestDto;
import ru.practicum.dto.request.RequestUpdateDto;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.ForbiddenException;
import ru.practicum.exception.ObjectNotFoundException;
import ru.practicum.mapper.EventMapper;
import ru.practicum.mapper.RequestMapper;
import ru.practicum.model.Category;
import ru.practicum.model.User;
import ru.practicum.model.event.Event;
import ru.practicum.model.event.EventState;
import ru.practicum.model.event.UserActionState;
import ru.practicum.model.request.Request;
import ru.practicum.model.request.RequestStatus;
import ru.practicum.repository.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PrivateEventService {
    private final RequestsRepository requestsRepository;
    private final LocationRepository locationRepository;
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Transactional
    public FullEventDto create(Long userId, CreateEventDto createEventDto) {
        if (createEventDto.getEventDate().isBefore(LocalDateTime.now())) {
            throw new ForbiddenException("Wrong date");
        }
        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new ObjectNotFoundException(String.format("Failed to find user with id=%s", userId));
        });
        Category category = categoryRepository.findById(createEventDto.getCategory()).orElseThrow(() -> {
            throw new ObjectNotFoundException(String.format("Failed to find category with id=%s",
                    createEventDto.getCategory()));
        });
        createEventDto.setLocation(locationRepository.save(createEventDto.getLocation()));
        Event event = eventRepository.save(EventMapper.EVENT_MAPPER.toEventFromCreateDto(user, category, createEventDto));
        FullEventDto fullEventDto = EventMapper.EVENT_MAPPER.toFullEventDto(event);
        fullEventDto.setConfirmedRequests(0);
        log.info("Event with Id {} is created", event.getId());
        return fullEventDto;
    }

    public List<ShortEventDto> getEventsByCreator(Long userId, Pageable pageable) {
        userRepository.findById(userId).orElseThrow(() -> {
            throw new ObjectNotFoundException(String.format("Failed to find user with id=%s", userId));
        });
        return eventRepository.findEventsByInitiatorId(userId, pageable).stream()
                .map(EventMapper.EVENT_MAPPER::toShortEventDto)
                .collect(Collectors.toList());
    }

    public FullEventDto getEventInfoByCreator(Long userId, Long eventId) {
        Event event = eventRepository.findEventByIdAndInitiatorId(eventId, userId).orElseThrow(() -> {
            throw new ObjectNotFoundException(String.format("Failed to find event with id=%s, user id=%s", eventId, userId));
        });
        return EventMapper.EVENT_MAPPER.toFullEventDto(event);
    }


    @Transactional
    public FullEventDto updateEvent(Long userId, Long eventId, EventUpdateRequestDto eventUpdateRequestDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new ObjectNotFoundException(String.format("Failed to find user with id=%s", userId));
        });
        Event event = eventRepository.findById(eventId).orElseThrow(() -> {
            throw new ObjectNotFoundException(String.format("Failed to find event with id=%s", eventId));
        });
        if (eventUpdateRequestDto.getEventDate() != null) {
            LocalDateTime time = LocalDateTime.parse(eventUpdateRequestDto.getEventDate(), dateTimeFormatter);
            if (LocalDateTime.now().isAfter(time.minusHours(2))) {
                throw new ForbiddenException("Event starts in less then 2 hours");
            }
        }
        if (event.getEventState().equals(EventState.PUBLISHED)) {
            throw new ForbiddenException("You can not change published events");
        }
        if (Objects.equals(eventUpdateRequestDto.getStateAction(), UserActionState.CANCEL_REVIEW.name())) {
            event.setEventState(EventState.CANCELED);
        }
        if (Objects.equals(eventUpdateRequestDto.getStateAction(), UserActionState.SEND_TO_REVIEW.name())) {
            event.setEventState(EventState.PENDING);
        }

        if (eventUpdateRequestDto.getTitle() != null) {
            event.setTitle(eventUpdateRequestDto.getTitle());
        }
        if (eventUpdateRequestDto.getAnnotation() != null) {
            event.setAnnotation(eventUpdateRequestDto.getAnnotation());
        }
        if (eventUpdateRequestDto.getDescription() != null) {
            event.setDescription(eventUpdateRequestDto.getDescription());
        }
        if (eventUpdateRequestDto.getCategory() != null) {
            Category category = categoryRepository.findById(eventUpdateRequestDto.getCategory()).orElseThrow(() -> {
                throw new ObjectNotFoundException(String.format("Failed to find category with id=%d",
                        eventUpdateRequestDto.getCategory()));
            });
            event.setCategory(category);
        }
        if (eventUpdateRequestDto.getEventDate() != null) {
            if (LocalDateTime.parse(eventUpdateRequestDto.getEventDate(), dateTimeFormatter)
                    .isBefore(LocalDateTime.now())) {
                throw new BadRequestException("date is in the past");
            } else {
                event.setEventDate(LocalDateTime.parse(eventUpdateRequestDto.getEventDate(),
                        dateTimeFormatter));
            }
        }
        if (eventUpdateRequestDto.getLocation() != null) {
            event.setLocation(eventUpdateRequestDto.getLocation());
        }
        if (eventUpdateRequestDto.getPaid() != null) {
            event.setPaid(eventUpdateRequestDto.getPaid());
        }
        if (eventUpdateRequestDto.getParticipantLimit() != null) {
            event.setParticipantLimit(eventUpdateRequestDto.getParticipantLimit());
        }
        if (eventUpdateRequestDto.getRequestModeration() != null) {
            event.setRequestModeration(eventUpdateRequestDto.getRequestModeration());
        }
        log.info("Event with Id {} is updated", event.getId());
        return EventMapper.EVENT_MAPPER.toFullEventDto(event);
    }


    public List<RequestDto> getEventRequests(Long userId, Long eventId) {
        return requestsRepository.findByEventIdAndInitiatorId(eventId, userId).stream()
                .map(RequestMapper.REQUEST_MAPPER::toRequestDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public RequestUpdateDto updateStatusRequest(Long userId, Long eventId,
                                              EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> {
            throw new ObjectNotFoundException("Event not found");
        });
        if (!event.getInitiator().getId().equals(userId)) {
            throw new ObjectNotFoundException("You don't have event with id " + eventId);
        }
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            throw new BadRequestException("Confirmation is not required");
        }
        RequestUpdateDto requestUpdateDto = new RequestUpdateDto(new ArrayList<>(), new ArrayList<>());
        Integer confirmedRequests = requestsRepository.findByEventIdConfirmed(eventId).size();
        List<Request> requests = requestsRepository.findByEventIdAndRequestsIds(eventId,
                eventRequestStatusUpdateRequest.getRequestIds());
        if (Objects.equals(eventRequestStatusUpdateRequest.getStatus(), RequestStatus.CONFIRMED.name())
                && confirmedRequests + requests.size() > event.getParticipantLimit()) {
            requests.forEach(request -> request.setStatus(RequestStatus.REJECTED));
            List<RequestDto> requestDto = requests.stream()
                    .map(RequestMapper.REQUEST_MAPPER::toRequestDto)
                    .collect(Collectors.toList());
            requestUpdateDto.setRejectedRequests(requestDto);
            requestsRepository.saveAll(requests);
            throw new ForbiddenException("Requests limit exceeded");
        }
        if (eventRequestStatusUpdateRequest.getStatus().equalsIgnoreCase(RequestStatus.REJECTED.name())) {
            requests.forEach(request -> {
                if (request.getStatus().equals(RequestStatus.CONFIRMED)) {
                    throw new ForbiddenException("You can't reject confirmed request");
                }
                request.setStatus(RequestStatus.REJECTED);
            });
            List<RequestDto> requestDto = requests.stream()
                    .map(RequestMapper.REQUEST_MAPPER::toRequestDto)
                    .collect(Collectors.toList());
            requestUpdateDto.setRejectedRequests(requestDto);
            requestsRepository.saveAll(requests);
        } else if (eventRequestStatusUpdateRequest.getStatus().equalsIgnoreCase(RequestStatus.CONFIRMED.name())
                && eventRequestStatusUpdateRequest.getRequestIds().size() <= event.getParticipantLimit() - confirmedRequests) {
            requests.forEach(request -> request.setStatus(RequestStatus.CONFIRMED));
            List<RequestDto> requestDto = requests.stream()
                    .map(RequestMapper.REQUEST_MAPPER::toRequestDto)
                    .collect(Collectors.toList());
            requestUpdateDto.setConfirmedRequests(requestDto);
            requestsRepository.saveAll(requests);
        }
        return requestUpdateDto;
    }

}
