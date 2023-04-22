package ru.practicum.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStatsDto;
import ru.practicum.mapper.StatsMapper;
import ru.practicum.model.EndpointHit;
import ru.practicum.repository.StatsRepository;

import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.util.List;


@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class StatsService {
    private final StatsRepository statsRepository;

    @Transactional
    public EndpointHitDto create(EndpointHitDto endpointHitDto) {
        EndpointHit endpointHit = statsRepository.save(StatsMapper.STATS_MAPPER.toEndpointHit(endpointHitDto));
        log.info("Hit created with id {}", endpointHit.getId());
        return StatsMapper.STATS_MAPPER.toEndpointHitDto(endpointHit);
        }



    public List<ViewStatsDto> getStats(LocalDateTime start,
                                       LocalDateTime end,
                                       List<String> uris,
                                       Boolean unique) {
        log.info("Stats sent");
        if (uris == null || uris.isEmpty()) {
            if (unique) {
                return statsRepository.getStatsWithoutUriUnique(start, end).stream()
                        .map(StatsMapper.STATS_MAPPER::toViewStatsDto)
                        .collect(Collectors.toList());
            } else {
                return statsRepository.getStatsWithoutUriNotUnique(start, end).stream()
                        .map(StatsMapper.STATS_MAPPER::toViewStatsDto)
                        .collect(Collectors.toList());
            }
        } else if (unique) {
            return statsRepository.getStatsUnique(start, end, uris).stream()
                    .map(StatsMapper.STATS_MAPPER::toViewStatsDto)
                    .collect(Collectors.toList());
        } else {
            return statsRepository.getStatsNotUnique(start, end, uris).stream()
                    .map(StatsMapper.STATS_MAPPER::toViewStatsDto)
                    .collect(Collectors.toList());
        }
    }
}


