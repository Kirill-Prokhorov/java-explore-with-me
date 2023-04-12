package ru.practicum.ewm.stat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.stat.dto.StatClientDto;
import ru.practicum.ewm.stat.model.Stat;
import ru.practicum.ewm.stat.service.StatService;

import static ru.practicum.ewm.stat.util.Constants.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@Slf4j
public class StatController {

    private final StatService statService;

    @PostMapping("/hit")
    public StatClientDto save(@Valid @RequestBody StatClientDto statClientDto) {
        log.info("URI в контроллере статистики: " + statClientDto.getUri());
        return statService.saveStat(statClientDto);
    }

    @GetMapping("/stat")
    public List<Stat> get(@RequestParam @DateTimeFormat(pattern = DATETIME_FORMAT) LocalDateTime start,
                          @RequestParam @DateTimeFormat(pattern = DATETIME_FORMAT) LocalDateTime end,
                          @RequestParam(required = false) List<String> uris,
                          @RequestParam(defaultValue = "false") Boolean unique) {
        log.info("Запрос на получение статистики");
        return statService.getStat(start, end, uris, unique);
    }

}
