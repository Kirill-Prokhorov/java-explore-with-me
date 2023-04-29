package ru.practicum.ewm.services.compilation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.entity.dto.compilation.CompilationDto;
import ru.practicum.ewm.entity.dto.compilation.NewCompilationDto;
import ru.practicum.ewm.entity.dto.compilation.UpdateCompilationRequest;
import ru.practicum.ewm.entity.mapper.CompilationMapper;
import ru.practicum.ewm.entity.model.Compilation;
import ru.practicum.ewm.entity.model.Event;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.repositorys.CompilationRepository;
import ru.practicum.ewm.repositorys.event.EventRepository;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Transactional
@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final CompilationMapper compilationMapper;

    @Override
    public CompilationDto post(NewCompilationDto newCompilationDto) {
        Set<Event> events;
        Compilation compilation = Compilation.builder().build();

        if (newCompilationDto.getEvents() != null && !newCompilationDto.getEvents().isEmpty()) {
            events = eventRepository.findByIdIn(newCompilationDto.getEvents());
            compilation.setEvents(events);
        } else {
            compilation.setEvents(new HashSet<>());
        }

        if (newCompilationDto.getPinned() != null) {
            compilation.setPinned(newCompilationDto.getPinned());
        }

        if (newCompilationDto.getTitle() != null) {
            compilation.setTitle(newCompilationDto.getTitle());
        }

        return compilationMapper.toDto(compilationRepository.save(compilation));
    }

    @Override
    public void deleteById(Long compilationId) {
        compilationRepository.deleteById(compilationId);
    }

    @Override
    public CompilationDto patch(Long compilationId,
                                UpdateCompilationRequest request) {
        Compilation compilation = compilationRepository.findById(compilationId)
                .orElseThrow(() -> new NotFoundException("Подборка событий не существует"));
        Set<Event> events;

        if (request.getEvents() != null) {
            events = eventRepository.findByIdIn(request.getEvents());
            compilation.setEvents(events);
        }

        if (request.getPinned() != null) {
            compilation.setPinned(request.getPinned());
        }

        if (request.getTitle() != null) {
            compilation.setTitle(request.getTitle());
        }

        return compilationMapper.toDto(compilationRepository.save(compilation));
    }

    @Override
    public CompilationDto getById(Long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Подборка событий не существует"));
        return compilationMapper.toDto(compilation);
    }

    @Override
    public List<CompilationDto> getAllByParams(Boolean pinned, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from, size);
        if (pinned != null && pinned) {
            return compilationRepository.findAllByPinned(pinned, pageable).stream()
                    .map(compilationMapper::toDto)
                    .collect(Collectors.toList());
        } else {
            return compilationRepository.findAll(pageable).stream()
                    .map(compilationMapper::toDto)
                    .collect(Collectors.toList());
        }
    }
}
