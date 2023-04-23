package ru.practicum.ewm.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.service.CategoryService;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
@Slf4j
public class CategoryPublicController {

    private final CategoryService categoryService;


    @GetMapping("/{catId}")
    public CategoryDto getById(@NotNull @PathVariable(name = "catId") Long id) {
        log.info("Общий запрос категории: получить категорию по id");
        return categoryService.getById(id);
    }

    @GetMapping
    public List<CategoryDto> getAllCategories(@PositiveOrZero @RequestParam(name = "from", defaultValue = "0")
                                              Integer index,
                                              @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Общий запрос категории: получить все категории");
        return categoryService.getAllCategories(index, size);
    }

}
