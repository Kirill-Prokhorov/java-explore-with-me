package ru.practicum.ewm.stat.dto;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.stat.model.StatClient;

@Component
public class StatMapper {

    public static StatClient toClass(StatClientDto statClientDto) {
        return StatClient.builder()
                .id(statClientDto.getId())
                .app(statClientDto.getApp())
                .uri(statClientDto.getUri())
                .ip(statClientDto.getIp())
                .timestamp(statClientDto.getTimestamp())
                .build();
    }

    public static StatClientDto toDto(StatClient statClient) {
        return StatClientDto.builder()
                .id(statClient.getId())
                .app(statClient.getApp())
                .uri(statClient.getUri())
                .ip(statClient.getIp())
                .timestamp(statClient.getTimestamp())
                .build();
    }

}
