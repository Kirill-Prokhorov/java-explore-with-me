package ru.practicum.ewm.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.practicum.ewm.ViewStatsDTO;
import ru.practicum.ewm.model.ViewStats;

@Mapper
public interface ViewStatsMapper {

    ViewStatsMapper VIEW_STATS_MAPPER = Mappers.getMapper(ViewStatsMapper.class);

    ViewStatsDTO toDto(ViewStats viewStats);
}
