package ru.practicum.mapper;

import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.ResponseCompilationDto;
import ru.practicum.dto.event.ShortEventDto;
import ru.practicum.model.Compilation;
import ru.practicum.model.event.Event;

import java.util.List;
import java.util.stream.Collectors;

public class CompilationMapper {
    public static Compilation toCompilation(CompilationDto compilationDto, List<Event> eventList) {
        return new Compilation(null,
                eventList,
                compilationDto.getPinned(),
                compilationDto.getTitle());
    }

    public static ResponseCompilationDto toResponseCompilationDto(Compilation compilation) {
        List<ShortEventDto> shortEventDtos = compilation.getEvents().stream()
                .map(EventMapper.EVENT_MAPPER::toShortEventDto)
                .collect(Collectors.toList());
        return new ResponseCompilationDto(compilation.getId(),
                shortEventDtos,
                compilation.getPinned(),
                compilation.getTitle());
    }
}
