package ru.practicum.ewm.repositorys.event;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.entity.enums.Status;
import ru.practicum.ewm.entity.model.Event;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface EventRepository extends CustomEventRepository, JpaRepository<Event, Long> {
    List<Event> findByInitiatorId(Long userId, Pageable pageable);

    Optional<Event> findByIdAndInitiatorId(Long userId, Long eventId);

    Set<Event> findByIdIn(Set<Long> events);

    Optional<Event> findByIdAndState(Long eventId, Status published);
}
