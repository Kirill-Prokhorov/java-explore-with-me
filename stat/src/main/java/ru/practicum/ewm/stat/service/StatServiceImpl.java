package ru.practicum.ewm.stat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.stat.dto.StatClientDto;
import ru.practicum.ewm.stat.dto.StatMapper;
import ru.practicum.ewm.stat.model.Stat;
import ru.practicum.ewm.stat.model.StatClient;
import ru.practicum.ewm.stat.repository.StatRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatServiceImpl implements StatService {

    private final StatRepository statRepository;

    @Transactional
    @Override
    public StatClientDto saveStat(StatClientDto statClientDto) {
        StatClient statClient = StatMapper.toClass(statClientDto);
        statClient.setTimestamp(LocalDateTime.now());
        log.info("MAPPED");
        statRepository.save(statClient);
        log.info("SAVED");
        return StatMapper.toDto(statClient);
    }

    @Override
    public List<Stat> getStat(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (unique) {
            log.info("GETTING UNIQUE");
            return statRepository.getAllUniqueStat(start, end, uris, true);
        } else {
            log.info("GETTING NOT UNIQUE");
            return statRepository.getAllStat(start, end, uris);
        }
    }

}
