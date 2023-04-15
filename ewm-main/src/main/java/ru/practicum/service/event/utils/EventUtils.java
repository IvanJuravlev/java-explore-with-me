package ru.practicum.service.event.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.practicum.ViewStatsDto;
import ru.practicum.dto.event.FullEventDto;
import ru.practicum.model.request.Request;
import ru.practicum.repository.RequestsRepository;
import ru.practicum.statistics.StatService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EventUtils {

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static final LocalDateTime MAX_TIME = toTime("5000-01-01 00:00:00");

    public static final LocalDateTime MIN_TIME = toTime("2000-01-01 00:00:00");

    public static List<FullEventDto> getViews(List<FullEventDto> eventDtos, StatService statService) {
        Map<String, FullEventDto> views = eventDtos.stream()
                .collect(Collectors.toMap(fullEventDto -> "/events/" + fullEventDto.getId(),
                        fullEventDto -> fullEventDto));

        Object responseBody = getObject(statService, views);

        List<ViewStatsDto> viewStatsDtos = new ObjectMapper().convertValue(responseBody, new TypeReference<>() {
        });
        viewStatsDtos.forEach(viewStatsDto -> {
            if (views.containsKey(viewStatsDto.getUri())) {
                views.get(viewStatsDto.getUri()).setViews(viewStatsDto.getHits());
            }
        });
        return new ArrayList<>(views.values());
    }

    private static Object getObject(StatService statService, Map<String, FullEventDto> views) {
        Object responseBody = statService.getViewStats(toString(MIN_TIME),
                        toString(MAX_TIME),
                        new ArrayList<>(views.keySet()),
                        false)
                .getBody();
        return responseBody;
    }

    public static String toString(LocalDateTime localDateTime) {
        return localDateTime.format(FORMATTER);
    }

    public static LocalDateTime toTime(String timeString) throws DateTimeParseException {
        return LocalDateTime.parse(timeString, FORMATTER);
    }

    public static void getConfirmedRequests(List<FullEventDto> eventDtos,
                                            RequestsRepository requestsRepository) {
        List<Long> ids = eventDtos.stream()
                .map(FullEventDto::getId)
                .collect(Collectors.toList());
        List<Request> requests = requestsRepository.findConfirmedToListEvents(ids);
        Map<Long, Integer> counter = new HashMap<>();
        requests.forEach(element -> counter.put(element.getEvent().getId(),
                counter.getOrDefault(element.getEvent().getId(), 0) + 1));
        eventDtos.forEach(event -> event.setConfirmedRequests(counter.get(event.getId())));
    }






}
