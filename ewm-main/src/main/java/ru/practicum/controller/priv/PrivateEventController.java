package ru.practicum.controller.priv;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.*;
import ru.practicum.dto.request.RequestDto;
import ru.practicum.service.event.PrivateEventService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
public class PrivateEventController {
    private final PrivateEventService privateEventService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FullEventDto createEvent(@PathVariable Long userId,
                                    @Valid @RequestBody CreateEventDto createEventDto) {

        return privateEventService.create(userId, createEventDto);
    }

    @GetMapping
    public List<ShortEventDto> getEventsByCreator(@Positive @PathVariable Long userId,
                                                  @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                  @Positive @RequestParam(defaultValue = "10") Integer size) {
        PageRequest pageable = PageRequest.of(from / size, size);

        return privateEventService.getEventsByCreator(userId, pageable); //возможно нужно через кастом пейдж реквест
    }

    @GetMapping("/{eventId}")
    public FullEventDto getEvent(@PathVariable Long userId,
                                 @PathVariable Long eventId) {

        return privateEventService.getEventInfoByCreator(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public FullEventDto updateEvent(@PathVariable Long userId,
                                    @PathVariable Long eventId,
                                    @RequestBody EventUpdateRequestDto updateEventUserRequest) {

        return privateEventService.updateEvent(userId, eventId, updateEventUserRequest);
    }

    @GetMapping("/{eventId}/requests")
    public List<RequestDto> getEventRequestsByCurrentUser(@PathVariable Long userId,
                                                          @PathVariable Long eventId) {

        return privateEventService.getEventRequests(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult changeStatus(@PathVariable Long userId,
                                                       @PathVariable Long eventId,
                                                       @RequestBody EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {

        return privateEventService.updateStatusRequest(userId, eventId, eventRequestStatusUpdateRequest);
    }
}
