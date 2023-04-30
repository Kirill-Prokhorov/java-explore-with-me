package ru.practicum.ewm.repositorys;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.entity.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
