package ru.practicum.ewm.services.category;

import ru.practicum.ewm.entity.dto.category.CategoryDto;
import ru.practicum.ewm.entity.dto.category.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto save(NewCategoryDto categoryDto);

    void deleteById(Long catId);

    CategoryDto patch(CategoryDto categoryDto, Long catId);

    List<CategoryDto> findAll(Integer from, Integer size);

    CategoryDto findById(Long catId);
}
