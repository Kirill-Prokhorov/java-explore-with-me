package ru.practicum.ewm.repositorys;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.entity.model.Subscription;

import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    Optional<Subscription> findByFollowerIdAndPublisherId(Long userId, Long publisherId);

    @Query(value = "select s from Subscription s left join fetch s.publisher where s.follower.id = ?1")
    List<Subscription> findByFollowerId(Long userId);
}