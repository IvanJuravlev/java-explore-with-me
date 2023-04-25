package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.request.RequestDto;
import ru.practicum.exception.ForbiddenException;
import ru.practicum.exception.ObjectNotFoundException;
import ru.practicum.mapper.RequestMapper;
import ru.practicum.model.User;
import ru.practicum.model.event.Event;
import ru.practicum.model.event.EventState;
import ru.practicum.model.request.Request;
import ru.practicum.model.request.RequestStatus;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.RequestsRepository;
import ru.practicum.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RequestsService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final RequestsRepository requestsRepository;

    @Transactional
    public RequestDto createRequest(Long userId, Long eventId) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new ObjectNotFoundException(String.format("User with id %x not found", userId));
        });
        Event event = eventRepository.findById(eventId).orElseThrow(() -> {
            throw new ObjectNotFoundException(String.format("Event with id %x not found", eventId));
        });
        if (event.getEventState() != EventState.PUBLISHED) {
            throw new ForbiddenException("You can't participate in an unpublished event");
        }
        if (Objects.equals(event.getInitiator().getId(), userId)) {
            throw new ForbiddenException("You can't send request to your own event");
        }
        int confirmedRequests = requestsRepository.findByEventIdConfirmed(eventId).size();
        if (event.getParticipantLimit() != 0 && event.getParticipantLimit() <= confirmedRequests) {
            throw new ForbiddenException("Reached participant limit for this event");
        }
        RequestStatus requestStatus = RequestStatus.PENDING;
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            requestStatus = RequestStatus.CONFIRMED;
        }
        Request request = new Request(null, LocalDateTime.now(), event, user, requestStatus);
        Optional<Request> check = requestsRepository.findByEventIdAndRequesterId(eventId, userId);
        if (check.isPresent()) throw new ForbiddenException("You already have request to this event");
        request = requestsRepository.save(request);
        log.info("Request with Id {} created", request.getId());
        return RequestMapper.REQUEST_MAPPER.toRequestDto(request);
    }

    public List<RequestDto> findByRequesterId(Long userId) {
        log.info("Request has been sent");
        return requestsRepository.findByRequesterId(userId).stream()
                .map(RequestMapper.REQUEST_MAPPER::toRequestDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public RequestDto cancelRequest(Long userId, Long requestId) {
        Request request = requestsRepository.findByIdAndRequesterId(requestId, userId).orElseThrow(() -> {
            throw new ObjectNotFoundException(String.format("Request with id %x not found", requestId));
        });
        request.setStatus(RequestStatus.CANCELED);
        log.info("Request with id {} canceled", requestId);
        return RequestMapper.REQUEST_MAPPER.toRequestDto(request);
    }

}
