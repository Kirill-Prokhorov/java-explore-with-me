package ru.practicum.ewm.entity.mapper;

import org.mapstruct.Mapper;
import ru.practicum.ewm.entity.dto.subscription.SubscriptionDto;
import ru.practicum.ewm.entity.model.Subscription;

@Mapper(componentModel = "spring")
public interface SubscriptionMapper {
    SubscriptionDto toDto(Subscription subscription);
}
