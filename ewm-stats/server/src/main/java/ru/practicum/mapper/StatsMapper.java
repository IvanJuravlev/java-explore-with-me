package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStatsDto;
import ru.practicum.model.EndpointHit;
import ru.practicum.model.ViewStats;

@Mapper
public interface StatsMapper {

    StatsMapper STATS_MAPPER = Mappers.getMapper(StatsMapper.class);

    EndpointHitDto toEndpointHitDto(EndpointHit endpointHit);

    EndpointHit toEndpointHit(EndpointHitDto endpointHitDto);

    ViewStatsDto toViewStatsDto(ViewStats viewStats);
}
