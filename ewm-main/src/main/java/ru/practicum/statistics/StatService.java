package ru.practicum.statistics;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.EndpointHitDto;
import ru.practicum.StatsClient;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StatService {
    private final StatsClient statsClient;

    public ResponseEntity<Object> getViewStats(
            String rangeStart,
            String rangeEnd,
            List<String> uris,
            Boolean unique) {
        log.info("StatsService - method call 'getViewStats' with params: rangeStart={}, rangeEnd={}, uris={}, " +
                "unique={}", rangeStart, rangeEnd, uris, unique);
        return statsClient.getViewStats(rangeStart, rangeEnd, uris, unique);
    }

    @Transactional
    public void createView(EndpointHitDto endpointHitDto) {
        log.info("StatsService - method call 'createView' with params: endpointHitDto={}", endpointHitDto);
        statsClient.create(endpointHitDto);
    }
}
