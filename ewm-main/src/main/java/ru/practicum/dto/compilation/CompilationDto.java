package ru.practicum.dto.compilation;

import javax.validation.constraints.NotBlank;

import lombok.*;

import java.util.List;

import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompilationDto {
    List<Long> events;
    Boolean pinned;
    @NotBlank
    String title;
}

