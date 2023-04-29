package ru.practicum.ewm.services.category;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.entity.dto.category.CategoryDto;
import ru.practicum.ewm.entity.dto.category.NewCategoryDto;
import ru.practicum.ewm.entity.mapper.CategoryMapper;
import ru.practicum.ewm.entity.model.Category;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.repositorys.CategoryRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;


    @Override
    public CategoryDto save(NewCategoryDto categoryDto) {
        Category category = categoryMapper.fromNewDto(categoryDto);

        return categoryMapper.toDto(categoryRepository.save(category));
    }

    @Override
    public void deleteById(Long catId) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Категория не существует!"));

        categoryRepository.deleteById(catId);
    }

    @Override
    public CategoryDto patch(CategoryDto categoryDto, Long catId) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Категория не существует!"));

        if (categoryDto.getName() != null)
            category.setName(categoryDto.getName());

        return categoryMapper.toDto(categoryRepository.save(category));
    }

    @Override
    public List<CategoryDto> findAll(Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from, size);

        return categoryRepository.findAll(pageable).stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto findById(Long catId) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Категория не существует!"));

        return categoryMapper.toDto(category);
    }
}
