package ru.practicum.dto.event;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.user.ShortUserDto;
import ru.practicum.model.Location;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FullEventDto {

    Long id;

    String annotation;

    CategoryDto category;

    Integer confirmedRequests;

    String createdOn;

    String description;

    String eventDate;

    ShortUserDto initiator;

    Location location;

    Boolean paid;

    Integer participantLimit;

    String publishedOn;

    Boolean requestModeration;

    String state;

    String title;

    Long views;
}
