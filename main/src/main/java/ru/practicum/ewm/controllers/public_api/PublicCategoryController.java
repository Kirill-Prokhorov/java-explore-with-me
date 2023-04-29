package ru.practicum.ewm.controllers.public_api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.entity.dto.category.CategoryDto;
import ru.practicum.ewm.services.category.CategoryService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/categories")
@Validated
public class PublicCategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryDto> findAll(@PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                     @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("PublicAPI CategoryController, getAll from: {}, size: {}", from, size);
        return categoryService.findAll(from, size);
    }

    @GetMapping("/{catId}")
    public CategoryDto findById(@Positive @PathVariable Long catId) {
        log.info("PublicAPI CategoryController, getById catId: {}", catId);
        return categoryService.findById(catId);
    }
}
