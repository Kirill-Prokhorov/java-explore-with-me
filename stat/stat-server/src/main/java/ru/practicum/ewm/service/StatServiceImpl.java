package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.EndpointHitDTO;
import ru.practicum.ewm.ViewStatsDTO;
import ru.practicum.ewm.mapper.EndpointMapper;
import ru.practicum.ewm.mapper.ViewStatsMapper;
import ru.practicum.ewm.model.EndpointHit;
import ru.practicum.ewm.repository.StatRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatServiceImpl implements StatService {

    private final StatRepository statRepository;

    @Override
    public EndpointHitDTO postHit(EndpointHitDTO endpointHitDTO) {
        EndpointHit endpointHit = EndpointMapper.ENDPOINT_MAPPER.fromDto(endpointHitDTO);
        return EndpointMapper.ENDPOINT_MAPPER.toEndpointHitDto(statRepository.save(endpointHit));
    }

    @Override
    public List<ViewStatsDTO> getStats(LocalDateTime start,
                                       LocalDateTime end,
                                       List<String> uris,
                                       boolean unique) {

        return statRepository.getStats(start, end, uris, unique).stream()
                .map(ViewStatsMapper.VIEW_STATS_MAPPER::toDto)
                .collect(Collectors.toList());
    }
}
