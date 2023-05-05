package ru.practicum.ewm.controllers.private_api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.entity.dto.subscription.SubscriptionDto;
import ru.practicum.ewm.services.subscription.SubscriptionService;

import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}/subscriptions")
@Validated
public class PrivateSubscriptionController {

    private final SubscriptionService subscriptionService;

    @GetMapping
    public List<SubscriptionDto> getFollowerSubscriptions(@Positive @PathVariable Long userId) {
        log.info("PrivateAPI SubscriptionController, getFollowerSubscriptions userId: {}", userId);
        return subscriptionService.findByFollowerId(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SubscriptionDto postSubscription(@Positive @PathVariable Long userId,
                                            @Positive @RequestParam Long publisherId) {
        log.info("PrivateAPI SubscriptionController, postSubscription userId: {}, publisherId: {}", userId, publisherId);
        return subscriptionService.postSubscription(userId, publisherId);
    }

    @PatchMapping("/{subscriptionId}/cancel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelSubscription(@Positive @PathVariable Long userId,
                                   @Positive @PathVariable Long subscriptionId) {
        log.info("PrivateAPI SubscriptionController, patch userId: {}, subscriptionId: {}", userId, subscriptionId);
        subscriptionService.cancelSubscription(userId, subscriptionId);
    }
}
