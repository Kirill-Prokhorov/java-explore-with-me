package ru.practicum.ewm.entity.dto.subscription;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.ewm.entity.dto.user.UserShortDto;

import java.time.LocalDateTime;

@ToString
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionDto {
    private Long id;
    @JsonIgnore
    private LocalDateTime created;
    @JsonIgnore
    private UserShortDto follower;
    private UserShortDto publisher;
}