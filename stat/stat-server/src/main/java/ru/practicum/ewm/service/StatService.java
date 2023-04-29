package ru.practicum.ewm.service;

import ru.practicum.ewm.EndpointHitDTO;
import ru.practicum.ewm.ViewStatsDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface StatService {
    EndpointHitDTO postHit(EndpointHitDTO endpointHitDTO);

    List<ViewStatsDTO> getStats(LocalDateTime parse, LocalDateTime parse1, List<String> uris, boolean unique);
}
