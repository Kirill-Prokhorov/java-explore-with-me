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

        if (subscriptionRepository.findByFollowerIdAndPublisherId(userId, publisherId).isPresent()) {
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
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не существует!"));
        return subscriptionRepository.findByFollowerId(userId).stream()
                .map(subscriptionMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void cancelSubscription(Long userId, Long subscriptionId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не существует!"));
        subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new NotFoundException("Подписка не существует!"));
        subscriptionRepository.deleteById(subscriptionId);
    }
}