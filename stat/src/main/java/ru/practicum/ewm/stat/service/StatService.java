package ru.practicum.ewm.stat.service;

import ru.practicum.ewm.stat.dto.StatClientDto;
import ru.practicum.ewm.stat.model.Stat;

import java.time.LocalDateTime;
import java.util.List;

public interface StatService {

    StatClientDto saveStat(StatClientDto statClientDto);

    List<Stat> getStat(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);

}
