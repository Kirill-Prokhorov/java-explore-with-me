package ru.practicum.ewm.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.EventNotFoundException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.UserNotFoundException;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.request.dto.RequestDto;
import ru.practicum.ewm.request.dto.RequestMapper;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.model.State;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Transactional
    @Override
    public RequestDto createRequest(Long userId, Long eventId) {
        log.info("Запрос на создание запроса");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("No such user found"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("No such event found"));
        Request request = Request.builder()
                .author(user)
                .event(event)
                .created(LocalDateTime.now())
                .state(State.PENDING)
                .build();
        if (user.getId().equals(event.getInitiator().getId()) ||
                !event.getState().equals(ru.practicum.ewm.event.model.State.PUBLISHED)) {
            throw new ValidationException("Validation not passed");
        }
        if (!event.getModeration() || event.getParticipantLimit() == 0) {
            request.setState(State.CONFIRMED);
        }
        log.info("Создание запроса");
        return RequestMapper.toDto(requestRepository.save(request));
    }

    @Transactional
    @Override
    public RequestDto cancelRequest(Long userId, Long requestId) {
        log.info("Запрос на отмену запроса");
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос не найден"));
        request.setState(State.CANCELED);
        log.info("Отмена запроса");
        return RequestMapper.toDto(requestRepository.save(request));
    }

    @Override
    public List<RequestDto> getUsersRequests(Long userId) {
        log.info("Запрос на все запросы пользователя");
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        return requestRepository.getAllByAuthor(author).stream()
                .map(RequestMapper::toDto)
                .collect(Collectors.toList());
    }

}
