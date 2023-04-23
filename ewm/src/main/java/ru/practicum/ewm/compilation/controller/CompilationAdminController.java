package ru.practicum.ewm.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.compilation.dto.CompilationCreationDto;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.service.CompilationService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/admin/compilations")
@RequiredArgsConstructor
@Slf4j
public class CompilationAdminController {

    private final CompilationService compilationService;

    @PostMapping
    public CompilationDto createCompilation(@RequestBody @Valid CompilationCreationDto compilationCreationDto) {
        log.info("Запрос администратора подборки: создание подборки");
        return compilationService.createCompilation(compilationCreationDto);
    }

    @PatchMapping("/{compId}/events/{eventId}")
    public CompilationDto addEventToCompilation(@PathVariable @NotNull Long compId,
                                                @PathVariable @NotNull Long eventId) {
        log.info("Запрос администратора подборки: добавление события в подборку");
        return compilationService.addEventToCompilation(compId, eventId);
    }

    @PatchMapping("/{compId}/pin")
    public CompilationDto pinCompilation(@PathVariable @NotNull Long compId) {
        log.info("Запрос администратора подборки: закреп подборки");
        return compilationService.pinCompilation(compId);
    }

    @DeleteMapping("/{compId}")
    public void deleteCompilation(@PathVariable @NotNull Long compId) {
        log.info("Запрос администратора подборки: удаление подборки");
        compilationService.deleteCompilation(compId);
    }


    @DeleteMapping("/{compId}/events/{eventId}")
    public void deleteEventFromCompilation(@PathVariable @NotNull Long compId,
                                           @PathVariable @NotNull Long eventId) {
        log.info("Запрос администратора подборки: удаление события из подборки");
        compilationService.deleteEventFromCompilation(compId, eventId);
    }

    @DeleteMapping("/{compId}/pin")
    public void unpinCompilation(@PathVariable @NotNull Long compId) {
        log.info("Запрос администратора подборки: откреп подборки");
        compilationService.unpinCompilation(compId);
    }

}
