package ru.practicum.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.event.FullEventDto;
import ru.practicum.exception.BadRequestException;
import ru.practicum.mapper.EventMapper;
import ru.practicum.model.event.Event;
import ru.practicum.model.event.EventState;
import ru.practicum.repository.AdminEventRepository;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.service.utils.DateFormatter;


import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminEventService {
    private final EventRepository eventRepository;
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
