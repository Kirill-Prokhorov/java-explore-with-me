package ru.practicum.ewm.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.service.CategoryService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/categories")
@Slf4j
public class CategoryAdminController {

    private final CategoryService categoryService;

    @PostMapping
    public CategoryDto create(@RequestBody @Valid CategoryDto categoryDto) {
        log.info("Запрос администратора категории: создание категории");
        return categoryService.create(categoryDto);
    }

    @PatchMapping
    public CategoryDto update(@RequestBody @Valid CategoryDto categoryDto) {
        log.info("Запрос администратора категории: обновление категории");
        return categoryService.update(categoryDto);
    }

    @DeleteMapping("/{catId}")
    public void delete(@PathVariable(name = "catId") @NotNull Long id) {
        log.info("Запрос администратора категории: удаление категории");
        categoryService.delete(id);
    }

}
