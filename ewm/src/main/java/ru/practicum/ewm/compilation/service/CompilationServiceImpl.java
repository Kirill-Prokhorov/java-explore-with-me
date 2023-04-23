package ru.practicum.ewm.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.compilation.dto.CompilationCreationDto;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.CompilationMapper;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.compilation.repository.CompilationRepository;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Transactional
    @Override
    public CompilationDto createCompilation(CompilationCreationDto compilationCreationDto) {
        log.info("Запрос на создание подборки");
        List<Event> events = eventRepository.getByIdIn(compilationCreationDto.getEvents());
        return CompilationMapper.toDto(compilationRepository.save(CompilationMapper.toClass(compilationCreationDto,
                events)));
    }

    @Transactional
    @Override
    public CompilationDto addEventToCompilation(Long compilationId, Long eventId) {
        log.info("Запрос на добавление события в подборку");
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EventNotFoundException("No such event"));
        Compilation compilation = retrieveCompilation(compilationId);
        List<Event> events = compilation.getEvents();
        for (Event event1 : events) {
            if (event1.equals(event)) {
                throw new EventAlreadyExistsException("Это событие есть в подборке");
            }
        }
        log.info("Валидация пройдена. Событие добавлено");
        events.add(event);
        compilation.setEvents(events);
        return CompilationMapper.toDto(compilationRepository.save(compilation));
    }

    @Transactional
    @Override
    public CompilationDto pinCompilation(Long compilationId) {
        log.info("Запрос на закреп подборки");
        Compilation compilation = retrieveCompilation(compilationId);
        if (compilation.getPinned()) {
            throw new CompilationAlreadyExistsException("Подборка уже закреплена");
        }
        log.info("Подборка найдена и закреплена");
        compilation.setPinned(true);
        return CompilationMapper.toDto(compilationRepository.save(compilation));
    }

    @Transactional
    @Override
    public void deleteCompilation(Long compilationId) {
        log.info("Запрос на удаление подборки");
        Compilation compilation = retrieveCompilation(compilationId);
        log.info("Подборка найдена и удалена");
        compilationRepository.delete(compilation);
    }

    @Transactional
    @Override
    public void deleteEventFromCompilation(Long compilationId, Long eventId) {
        log.info("Запрос на удаление события из подборки.");
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EventNotFoundException("No such event"));
        Compilation compilation = retrieveCompilation(compilationId);
        List<Event> events = compilation.getEvents();
        for (Event event1 : events) {
            if (event1.equals(event)) {
                log.info("Валидация пройдена, удаляем событие из подборки");
                events.remove(event);
                compilation.setEvents(events);
                compilationRepository.save(compilation);
                return;
            }
        }
        throw new EventNotFoundException("Этого события нет в подборке");
    }

    @Transactional
    @Override
    public void unpinCompilation(Long compilationId) {
        log.info("Запрос на откреп подборки");
        Compilation compilation = retrieveCompilation(compilationId);
        if (!compilation.getPinned()) {
            throw new CompilationAlreadyExistsException("Подборка не закреплена");
        }
        log.info("Подборка найдена и откреплена");
        compilation.setPinned(false);
        compilationRepository.save(compilation);
    }

    @Override
    public List<CompilationDto> getAllCompilations(Boolean pinned, Integer index, Integer size) {
        log.info("Запрос на получение всех подборок");
        Pageable pageable = PageRequest.of(index / size, size);
        if (pinned == null) {
            return compilationRepository.findAll(pageable)
                    .stream()
                    .map(CompilationMapper::toDto)
                    .collect(Collectors.toList());
        } else {
            return compilationRepository.findAllByPinned(pinned, pageable)
                    .stream()
                    .map(CompilationMapper::toDto)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public CompilationDto getCompilationById(Long compilationId) {
        log.info("Запрос на получение подборки по id");
        Compilation compilation = retrieveCompilation(compilationId);
        log.info("Подборка найдена");
        return CompilationMapper.toDto(compilation);
    }

    private Compilation retrieveCompilation(Long id) {
        return compilationRepository.findById(id).orElseThrow(() -> new NotFoundException("No such compilation found"));
    }

}
