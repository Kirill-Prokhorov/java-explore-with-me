package ru.practicum.ewm.services.subscription;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.entity.dto.subscription.SubscriptionDto;
import ru.practicum.ewm.entity.mapper.SubscriptionMapper;
import ru.practicum.ewm.entity.model.Subscription;
import ru.practicum.ewm.entity.model.User;
import ru.practicum.ewm.exception.ForbiddenException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.repositorys.SubscriptionRepository;
import ru.practicum.ewm.repositorys.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionMapper subscriptionMapper;

    @Override
    public SubscriptionDto postSubscription(Long userId, Long publisherId) {
        User follower = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не существует!"));
        User publisher = userRepository.findById(publisherId)
                .orElseThrow(() -> new NotFoundException("Пользователь не существует!"));

        if (subscriptionRepository.existsByFollowerIdAndPublisherId(userId, publisherId)) {
            throw new ForbiddenException("Подписка уже существует");
        }

        if (userId.equals(publisherId)) {
            throw new ForbiddenException("Нельзя подписываться на самого себя!");
        }
        Subscription subscription = Subscription.builder()
                .created(LocalDateTime.now())
                .follower(follower)
                .publisher(publisher)
                .build();
        return subscriptionMapper.toDto(subscriptionRepository.save(subscription));
    }

    @Override
    public List<SubscriptionDto> findByFollowerId(Long userId) {
        List<SubscriptionDto> subscriptionDto;
        if (userRepository.existsById(userId)) {
            subscriptionDto = subscriptionRepository.findByFollowerId(userId).stream()
                    .map(subscriptionMapper::toDto)
                    .collect(Collectors.toList());
        } else {
            throw new NotFoundException("Пользователь не существует!");
        }

        return subscriptionDto;
    }

    @Override
    public void cancelSubscription(Long userId, Long subscriptionId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь не существует!");
        }
        if (!userRepository.existsById(subscriptionId)) {
            throw new NotFoundException("Подписка не существует!");
        }
        subscriptionRepository.deleteById(subscriptionId);
    }
}