package ru.practicum.ewm.services.subscription;

import ru.practicum.ewm.entity.dto.subscription.SubscriptionDto;

import java.util.List;

public interface SubscriptionService {
    SubscriptionDto postSubscription(Long userId, Long publisherId);

    List<SubscriptionDto> findByFollowerId(Long userId);

    void cancelSubscription(Long userId, Long subscriptionId);
}
