package ru.practicum.ewm.controllers.admin_api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.entity.dto.compilation.CompilationDto;
import ru.practicum.ewm.entity.dto.compilation.NewCompilationDto;
import ru.practicum.ewm.entity.dto.compilation.UpdateCompilationRequest;
import ru.practicum.ewm.services.compilation.CompilationService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/compilations")
@Validated
public class AdminCompilationController {

    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto adminPostCompilation(@Valid @RequestBody NewCompilationDto newCompilationDto) {
        log.info("AdminAPI CompilationController, post newCompilationDto: {}", newCompilationDto.toString());
        return compilationService.post(newCompilationDto);
    }

    @DeleteMapping("/{comp}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void adminDeleteCompilation(@Positive @PathVariable Long comp) {
        log.info("AdminAPI CompilationController, delete id: {}", comp);
        compilationService.deleteById(comp);
    }

    @PatchMapping("/{comp}")
    public CompilationDto adminPatchCompilation(@Positive @PathVariable Long comp,
                                                @Valid @RequestBody UpdateCompilationRequest request) {
        log.info("AdminAPI CompilationController, patch id: {}, request: {} ", comp, request.toString());
        return compilationService.patch(comp, request);
    }
}