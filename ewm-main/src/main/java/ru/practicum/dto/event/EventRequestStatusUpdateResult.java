package ru.practicum.dto.event;


import lombok.*;

import java.util.List;

import lombok.experimental.FieldDefaults;
import ru.practicum.dto.request.RequestDto;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventRequestStatusUpdateResult {

    List<RequestDto> confirmedRequests;

    List<RequestDto> rejectedRequests;
}