package ru.practicum.ewm.entity.dto.subscription;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import ru.practicum.ewm.entity.dto.user.UserShortDto;

import java.time.LocalDateTime;

@Value
public class SubscriptionDto {
    Long id;
    @JsonIgnore
    LocalDateTime created;
    @JsonIgnore
    UserShortDto follower;
    UserShortDto publisher;
}