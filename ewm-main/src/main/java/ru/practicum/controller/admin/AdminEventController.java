package ru.practicum.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.EventUpdateRequestDto;
import ru.practicum.dto.event.FullEventDto;
import ru.practicum.model.event.EventState;
import ru.practicum.service.event.AdminEventService;
import ru.practicum.service.event.utils.CustomPageRequest;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@Validated
@RequestMapping("/admin/events")
@RequiredArgsConstructor
public class AdminEventController {
    private final AdminEventService adminEventService;

    @GetMapping
    public List<FullEventDto> getEvents(@RequestParam(required = false) List<Long> users,
                                        @RequestParam(required = false) List<EventState> states,
                                        @RequestParam(required = false) List<Long> categories,
                                        @RequestParam(required = false) String rangeStart,
                                        @RequestParam(required = false) String rangeEnd,
                                        @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                        @Positive @RequestParam(defaultValue = "10") Integer size) {
        PageRequest pageable = PageRequest.of(from / size, size);
        return adminEventService.getAdminEvents(users, states, categories, rangeStart, rangeEnd,
                /*new CustomPageRequest(from, size, Sort.unsorted())*/ pageable); //ВОзможно вернуть создание через CustomPageRequest
    }

    @PatchMapping("/{eventId}")
    public FullEventDto updateEvent(@PathVariable Long eventId,
                                    @RequestBody EventUpdateRequestDto eventDto) {
        return adminEventService.updateAdminEvent(eventId, eventDto);
    }
}
