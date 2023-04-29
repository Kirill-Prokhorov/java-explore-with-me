package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.model.EndpointHit;

public interface StatRepository extends JpaRepository<EndpointHit, Long>, CustomStatRepository {
}
