package ru.practicum.ewm.repositorys;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.entity.model.Location;

public interface LocationRepository extends JpaRepository<Location, Long> {
}
