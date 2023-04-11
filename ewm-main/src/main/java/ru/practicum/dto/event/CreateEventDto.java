package ru.practicum.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Positive;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.*;

import java.time.LocalDateTime;

import lombok.experimental.FieldDefaults;
import ru.practicum.model.Location;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateEventDto {
    @NotNull
    @Size(min = 5, max = 2000)
    String annotation;
    @NotNull
    @Positive
    Long category;
    @NotNull
    @Size(max = 5000)
    String description;
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime eventDate;
    @NotNull
    Location location;
    Boolean paid;
    @PositiveOrZero
    Integer participantLimit;
    Boolean requestModeration;
    @NotNull
    @Size(min = 5, max = 500)
    String title;
}
