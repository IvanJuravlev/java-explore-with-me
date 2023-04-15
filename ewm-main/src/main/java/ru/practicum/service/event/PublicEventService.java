package ru.practicum.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.event.FullEventDto;
import ru.practicum.dto.event.ShortEventDto;
import ru.practicum.exception.ForbiddenException;
import ru.practicum.exception.ObjectNotFoundException;
import ru.practicum.mapper.EventMapper;
import ru.practicum.model.event.Event;
import ru.practicum.model.event.EventState;
import ru.practicum.model.request.RequestStatus;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.RequestsRepository;
import ru.practicum.service.event.utils.EventUtils;
import ru.practicum.service.utils.DateFormatter;
import ru.practicum.statistics.HitMapper;
import ru.practicum.statistics.StatService;


import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PublicEventService {
    private final EventRepository eventRepository;
    private final StatService statsService;

    private final RequestsRepository requestsRepository;

    private final EventUtils eventUtils;


    public FullEventDto getPublicEvent(Long eventId, HttpServletRequest request) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> {
            throw new ObjectNotFoundException(String.format("Failed to find event with id=%d", eventId));
        });

        if (!event.getEventState().equals(EventState.PUBLISHED)) {
            throw new ObjectNotFoundException(String.format("Event with id: %s is not published", eventId));
        }
        FullEventDto fullEventDto = EventMapper.EVENT_MAPPER.toFullEventDto(event);
        fullEventDto.setConfirmedRequests(requestsRepository.findAllByEventIdAndStatus(event.getId(),
                RequestStatus.CONFIRMED).size());
        statsService.createView(HitMapper.toEndpointHit("ewm-main-service", request));

        return eventUtils.getViews(Collections.singletonList(fullEventDto), statsService).get(0);
    }


    public List<ShortEventDto> getPublicEvents(String text, List<Long> categories, Boolean paid,
                                               String rangeStart, String rangeEnd, Boolean onlyAvailable,
                                               String sort, Pageable pageable, HttpServletRequest request) {

        LocalDateTime start = rangeStart != null ? DateFormatter.toTime(rangeStart) : null;
        LocalDateTime end = rangeEnd != null ? DateFormatter.toTime(rangeEnd) : null;

        checkDateTimePeriod(start, end);

        List<Event> events = eventRepository.findPublicEvents(text.toLowerCase(), categories, paid,
                start, end, EventState.PUBLISHED, pageable);

        List<FullEventDto> fullEventDtoList = events.stream()
                .map(EventMapper.EVENT_MAPPER::toFullEventDto)
                .collect(Collectors.toList());
        fullEventDtoList.forEach(event -> event.setConfirmedRequests(requestsRepository
                .findByEventIdConfirmed(event.getId()).size()));

        if (onlyAvailable) {
            fullEventDtoList = fullEventDtoList.stream()
                    .filter(event -> event.getParticipantLimit() <= event.getConfirmedRequests())
                    .collect(Collectors.toList());
        }

        statsService.createView(HitMapper.toEndpointHit("ewm-main-service", request));
        List<ShortEventDto> eventsShort = eventUtils.getViews(fullEventDtoList, statsService).stream()
                .map(EventMapper.EVENT_MAPPER::toShortFromFull)
                .collect(Collectors.toList());
        if (sort != null && sort.equalsIgnoreCase("VIEWS")) {
            eventsShort.sort((e1, e2) -> e2.getViews().compareTo(e1.getViews()));
        }
        eventUtils.getConfirmedRequests(fullEventDtoList, requestsRepository);
        return eventsShort;

    }


    private void checkDateTimePeriod(LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        if (rangeStart != null && rangeEnd != null) {
            if (rangeStart.isAfter(rangeEnd)) {
                throw new ForbiddenException(
                        String.format("Start date: %s of the interval must be earlier than the end: %s date",
                                rangeStart, rangeEnd));
            }
        }
    }









}
