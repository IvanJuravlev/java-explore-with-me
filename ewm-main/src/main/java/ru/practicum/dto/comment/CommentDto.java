package ru.practicum.dto.comment;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentDto {
    Long id;

    String text;

    Long author;

    Long eventId;

    LocalDateTime createdOn;

    LocalDateTime updatedOn;
}