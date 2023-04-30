package ru.practicum.ewm.controllers.public_api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.entity.dto.compilation.CompilationDto;
import ru.practicum.ewm.services.compilation.CompilationService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/compilations")
@Validated
public class PublicCompilationController {
    private final CompilationService compilationService;

    @GetMapping
    public List<CompilationDto> publicGetCompilations(@RequestParam(required = false) Boolean pinned,
                                                      @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                      @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("PublicAPI CompilationController, getAll, pinned: {}", pinned);
        return compilationService.getAllByParams(pinned, from, size);
    }

    @GetMapping("/{compId}")
    public CompilationDto publicGetCompilation(@PathVariable Long compId) {
        log.info("PublicAPI CompilationController, getById, compId: {}", compId);
        return compilationService.getById(compId);
    }
}