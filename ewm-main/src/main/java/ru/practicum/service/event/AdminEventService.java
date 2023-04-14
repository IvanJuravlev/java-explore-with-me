package ru.practicum.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.event.EventUpdateRequestDto;
import ru.practicum.dto.event.FullEventDto;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.ObjectNotFoundException;
import ru.practicum.mapper.EventMapper;
import ru.practicum.model.Category;
import ru.practicum.model.event.AdminStateAction;
import ru.practicum.model.event.Event;
import ru.practicum.model.event.EventState;
import ru.practicum.repository.AdminEventRepository;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.service.utils.DateFormatter;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminEventService {
    private final EventRepository eventRepository;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final CategoryRepository categoryRepository;
    private final AdminEventRepository adminEventRepository;


    public List<FullEventDto> getAdminEvents(List<Long> users, List<EventState> states, List<Long> categories,
                                             String rangeStart, String rangeEnd, Pageable pageable) {

        LocalDateTime start = rangeStart != null ? DateFormatter.toTime(rangeStart) : null;
        LocalDateTime end = rangeEnd != null ? DateFormatter.toTime(rangeEnd) : null;

        checkDateTimePeriod(start, end);

        List<Event> events = eventRepository.findAdminEvents(users, states, categories, start, end, pageable);

        return events.stream()
                .map(EventMapper.EVENT_MAPPER::toFullEventDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public FullEventDto updateAdminEvent(Long eventId, EventUpdateRequestDto eventUpdateRequestDto) {
        Event event =  eventRepository.findById(eventId).orElseThrow(() -> {
            throw new ObjectNotFoundException(String.format("Failed to find event with id=%d", eventId));
        });
        if (eventUpdateRequestDto.getTitle() != null) {
            event.setTitle(eventUpdateRequestDto.getTitle());
        }
        if (eventUpdateRequestDto.getAnnotation() != null) {
            event.setAnnotation(eventUpdateRequestDto.getAnnotation());
        }
        if (eventUpdateRequestDto.getCategory() != null) {
            Category category = categoryRepository.findById(eventUpdateRequestDto.getCategory()).orElseThrow(() -> {
                throw new ObjectNotFoundException(String.format("Failed to find category with id=%d",
                        eventUpdateRequestDto.getCategory()));
                    });
            event.setCategory(category);
        }
        if (eventUpdateRequestDto.getDescription() != null) {
            event.setDescription(eventUpdateRequestDto.getDescription());
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
        if (eventUpdateRequestDto.getStateAction() != null) {
            if (eventUpdateRequestDto.getStateAction().equals(AdminStateAction.PUBLISH_EVENT.name())) {
                event.setEventState(EventState.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            } else if (eventUpdateRequestDto.getStateAction().equals(AdminStateAction.REJECT_EVENT.name())
                    && event.getEventState() != EventState.PUBLISHED) {
                event.setEventState(EventState.CANCELED);
            }
        }
        return EventMapper.EVENT_MAPPER.toFullEventDto(event);
    }

    private void checkDateTimePeriod(LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        if (rangeEnd != null && rangeEnd != null) {
            if (rangeStart.isAfter(rangeEnd)) {
                throw new BadRequestException(
                        String.format("Start date: %s of the interval must be earlier than the end: %s date",
                                rangeStart, rangeEnd));
            }
        }
    }


}
