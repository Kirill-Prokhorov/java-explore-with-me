package ru.practicum.ewm.entity.mapper;

import org.mapstruct.Mapper;
import ru.practicum.ewm.entity.dto.category.CategoryDto;
import ru.practicum.ewm.entity.dto.category.NewCategoryDto;
import ru.practicum.ewm.entity.model.Category;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryDto toDto(Category category);

    Category fromDto(CategoryDto categoryDto);

    Category fromNewDto(NewCategoryDto categoryDto);
}
