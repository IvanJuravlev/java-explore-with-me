package ru.practicum.dto.event;

import javax.validation.constraints.NotNull;

import lombok.*;

import java.util.List;

import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventRequestStatusUpdateRequest {

    @NotNull
    List<Long> requestIds;

    @NotNull
    String status;
}