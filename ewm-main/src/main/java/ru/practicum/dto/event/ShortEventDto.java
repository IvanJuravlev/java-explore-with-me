package ru.practicum.dto.event;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.user.ShortUserDto;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShortEventDto {

    Long id;

    String annotation;

    CategoryDto category;

    Integer confirmedRequests;

    String eventDate;

    ShortUserDto initiator;

    Boolean paid;

    String title;

    Long views;
}
