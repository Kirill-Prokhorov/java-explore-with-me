package ru.practicum.ewm.services.compilation;

import ru.practicum.ewm.entity.dto.compilation.CompilationDto;
import ru.practicum.ewm.entity.dto.compilation.NewCompilationDto;
import ru.practicum.ewm.entity.dto.compilation.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {
    CompilationDto post(NewCompilationDto newCompilationDto);

    void deleteById(Long compilationId);

    CompilationDto patch(Long comp, UpdateCompilationRequest request);

    CompilationDto getById(Long compId);

    List<CompilationDto> getAllByParams(Boolean pinned, Integer from, Integer size);
}
