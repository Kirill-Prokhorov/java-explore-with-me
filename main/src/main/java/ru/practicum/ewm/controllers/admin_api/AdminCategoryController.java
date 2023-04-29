package ru.practicum.ewm.controllers.admin_api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.entity.dto.category.CategoryDto;
import ru.practicum.ewm.entity.dto.category.NewCategoryDto;
import ru.practicum.ewm.services.category.CategoryService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/categories")
@Validated
public class AdminCategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto save(@RequestBody @Valid NewCategoryDto newCategoryDto) {
        log.info("AdminAPI CategoryController, post {}", newCategoryDto.toString());
        return categoryService.save(newCategoryDto);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@Positive @PathVariable Long catId) {
        log.info("AdminAPI CategoryController, delete id: {}", catId);
        categoryService.deleteById(catId);
    }

    @PatchMapping("/{catId}")
    public CategoryDto patch(@RequestBody @Valid CategoryDto categoryDto,
                             @Positive @PathVariable Long catId) {
        log.info("AdminAPI CategoryController, patch id: {}, categoryDto: {} ", catId, categoryDto.toString());
        return categoryService.patch(categoryDto, catId);
    }
}