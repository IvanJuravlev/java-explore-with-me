package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.ResponseCompilationDto;
import ru.practicum.exception.ObjectNotFoundException;
import ru.practicum.mapper.CompilationMapper;
import ru.practicum.model.Compilation;
import ru.practicum.model.event.Event;
import ru.practicum.repository.CompilationRepository;
import ru.practicum.repository.EventRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Transactional
    public ResponseCompilationDto create(CompilationDto compilationDto) {
        List<Event> events = eventRepository.findEventsByIds(compilationDto.getEvents());
        log.info("Compilation is created");
        return CompilationMapper.toResponseCompilationDto(
                compilationRepository.save(CompilationMapper.toCompilation(compilationDto, events)));
    }

    @Transactional
    public ResponseCompilationDto update(Long id, CompilationDto compilationDto) {
        Compilation compilation = compilationRepository.findById(id).orElseThrow(() -> {
            throw new ObjectNotFoundException(String.format("compilation with id %x not found", id));
        });
        if (compilationDto.getEvents() != null) {
            compilation.setEvents(eventRepository.findEventsByIds(compilationDto.getEvents()));
        }
        if (compilationDto.getPinned() != null) {
            compilation.setPinned(compilationDto.getPinned());
        }
        if (compilationDto.getTitle() != null) {
            compilation.setTitle(compilationDto.getTitle());
        }
        compilationRepository.save(compilation);
        log.info("compilation {} was updated", id);
        return CompilationMapper.toResponseCompilationDto(compilation);
    }

    @Transactional
    public void delete(Long id) {
        Compilation compilation = compilationRepository.findById(id).orElseThrow(() -> {
            throw new ObjectNotFoundException(String.format("compilation with id %x not found", id));
        });
        compilationRepository.deleteById(id);
        log.info("compilation with id {} was deleted", id);
    }

    public List<ResponseCompilationDto> findAll(Boolean pined, Pageable pageable) {
        log.info("compilation info been send");
        return compilationRepository.findAllByPinned(pined, pageable).stream()
                .map(CompilationMapper::toResponseCompilationDto)
                .collect(Collectors.toList());
    }

    public ResponseCompilationDto findById(Long id) {
        Compilation compilation = compilationRepository.findById(id).orElseThrow(() -> {
            throw new ObjectNotFoundException(String.format("compilation with id %x not found", id));
        });
        log.info("compilation with id {} been send", id);
        return CompilationMapper.toResponseCompilationDto(compilation);
    }
}
