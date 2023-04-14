package ru.practicum.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.event.CreateEventDto;
import ru.practicum.dto.event.EventUpdateRequestDto;
import ru.practicum.dto.event.FullEventDto;
import ru.practicum.dto.event.ShortEventDto;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.ForbiddenException;
import ru.practicum.exception.ObjectNotFoundException;
import ru.practicum.mapper.EventMapper;
import ru.practicum.model.Category;
import ru.practicum.model.User;
import ru.practicum.model.event.Event;
import ru.practicum.model.event.EventState;
import ru.practicum.model.event.UserActionState;
import ru.practicum.repository.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PrivateEventService {
    private final RequestsRepository repository;
    private final LocationRepository locationRepository;
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Transactional
    public FullEventDto create(Long userId, CreateEventDto createEventDto) {
        if (createEventDto.getEventDate().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Wrong date");
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
        return fullEventDto;
    }

    public List<ShortEventDto> getEventsByCreator(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new ObjectNotFoundException(String.format("Failed to find user with id=%s", userId));
        });
        List<Event> events = eventRepository.findEventsByInitiatorId(userId, pageable);
        return events.stream()
                .map(EventMapper.EVENT_MAPPER::toShortEventDto)
                .collect(Collectors.toList());
    }

    public FullEventDto getEventInfoByCreator(Long userId, Long eventId) {
        Event event = eventRepository.findEventByIdAndInitiatorId(eventId, userId).orElseThrow(() -> {
            throw new ObjectNotFoundException( String.format("Failed to find event with id=%s, user id=%s", eventId, userId));
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
                throw new BadRequestException("Event starts in less then 2 hours");
            }
        }
        if (event.getEventState().equals(EventState.PUBLISHED)) {
            throw new ForbiddenException("You can not change published events");
        }
        if (eventUpdateRequestDto.getStateAction() != null) {
            if (eventUpdateRequestDto.getStateAction().equals(UserActionState.SEND_TO_REVIEW)) {
                event.setEventState(EventState.PENDING);
            } else if (eventUpdateRequestDto.getStateAction().equals(UserActionState.CANCEL_REVIEW)) {
                event.setEventState(EventState.CANCELED);
            }
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
            if (LocalDateTime.parse(eventUpdateRequestDto.getEventDate(),dateTimeFormatter)
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
        return EventMapper.EVENT_MAPPER.toFullEventDto(event);
        }
}
