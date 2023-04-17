package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.StatsClientDto;
import ru.practicum.ewm.dto.StatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {

    StatsClientDto saveStats(StatsClientDto statsClientDto);

    List<StatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);

}
