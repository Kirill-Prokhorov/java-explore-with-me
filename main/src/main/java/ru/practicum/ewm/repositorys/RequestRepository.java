package ru.practicum.ewm.repositorys;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.entity.enums.RequestStatus;
import ru.practicum.ewm.entity.model.Request;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findByRequesterId(Long userId);

    List<Request> findByEventIdAndStatus(Long eventId, RequestStatus status);

    List<Request> findByEventIdInAndStatus(List<Long> eventsIds, RequestStatus status);

    List<Request> findByEventId(Long eventId);

    Optional<Request> findByRequesterIdAndEventId(Long userId, Long eventId);

    List<Request> findByIdIn(List<Long> requestIds);
}