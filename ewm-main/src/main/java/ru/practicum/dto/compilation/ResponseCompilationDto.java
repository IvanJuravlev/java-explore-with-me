package ru.practicum.dto.compilation;

import lombok.*;

import java.util.List;

import lombok.experimental.FieldDefaults;
import ru.practicum.dto.event.ShortEventDto;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ResponseCompilationDto {
    Long id;
    List<ShortEventDto> events;
    Boolean pinned;
    String title;
}
