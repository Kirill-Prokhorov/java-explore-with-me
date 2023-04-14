package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.StatsClientDto;
import ru.practicum.ewm.dto.StatsClientMapper;
import ru.practicum.ewm.dto.StatsDto;
import ru.practicum.ewm.model.Stats;
import ru.practicum.ewm.model.StatsClient;
import ru.practicum.ewm.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.ewm.dto.StatsMapper.STATS_MAPPER;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final StatsRepository statsRepository;

    @Transactional
    @Override
    public StatsClientDto saveStats(StatsClientDto statsClientDto) {
        StatsClient statsClient = StatsClientMapper.toClass(statsClientDto);
        statsClient.setTimestamp(LocalDateTime.now());
        statsRepository.save(statsClient);
        return StatsClientMapper.toDto(statsClient);
    }

    @Override
    public List<StatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {

        List<Stats> stats;

        if (uris == null || uris.isEmpty()) {
            stats = statsRepository.getAllStatsWithoutUris(start, end);
        } else if (unique) {
            stats = statsRepository.getAllUniqueStats(start, end, uris);
        } else {
            stats = statsRepository.getAllStats(start, end, uris);
        }
        return stats.stream()
                .map(STATS_MAPPER::toDto)
                .collect(Collectors.toList());

    }

}
