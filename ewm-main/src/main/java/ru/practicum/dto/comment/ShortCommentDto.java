package ru.practicum.dto.comment;

import java.time.LocalDateTime;

import lombok.*;
import lombok.experimental.FieldDefaults;



@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShortCommentDto {
    String text;
    LocalDateTime created;
}
