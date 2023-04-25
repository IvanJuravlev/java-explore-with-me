package ru.practicum.controller.priv;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.request.RequestDto;
import ru.practicum.service.RequestsService;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;

@Validated
@RestController
@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
public class PrivateRequestController {
    private final RequestsService requestsService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RequestDto createRequest(@Positive @PathVariable Long userId,
                                    @Positive @RequestParam Long eventId) {
        return requestsService.createRequest(userId, eventId);
    }

    @GetMapping
    public List<RequestDto> findByRequesterId(@Positive @PathVariable Long userId) {
        return requestsService.findByRequesterId(userId);
    }

    @PatchMapping("/{requestId}/cancel")
    public RequestDto cancelRequest(@PathVariable @Positive @NotNull Long userId,
                                    @PathVariable @Positive @NotNull Long requestId) {
        return requestsService.cancelRequest(userId, requestId);
    }
}
