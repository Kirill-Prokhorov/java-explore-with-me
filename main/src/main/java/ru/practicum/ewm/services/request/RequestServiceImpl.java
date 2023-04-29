package ru.practicum.ewm.services.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.entity.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.entity.enums.RequestStatus;
import ru.practicum.ewm.entity.enums.Status;
import ru.practicum.ewm.entity.mapper.RequestMapper;
import ru.practicum.ewm.entity.model.Event;
import ru.practicum.ewm.entity.model.Request;
import ru.practicum.ewm.entity.model.User;
import ru.practicum.ewm.exception.ForbiddenException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.repositorys.RequestRepository;
import ru.practicum.ewm.repositorys.UserRepository;
import ru.practicum.ewm.repositorys.event.EventRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final RequestMapper requestMapper;


    @Override
    public List<ParticipationRequestDto> findByRequesterId(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не существует!"));
        return requestRepository.findByRequesterId(userId).stream()
                .map(requestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto findByRequesterIdAndEventId(Long userId, Long eventId) {
        if (requestRepository.findByRequesterIdAndEventId(userId, eventId).isPresent()) {
            throw new ForbiddenException("Запрос на участие уже существует");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не существует!"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено!"));

        if (event.getInitiator().getId().equals(userId)) {
            throw new ForbiddenException("Инициатор события не может добавить запрос на участие в своём событии!");
        }

        if (!event.getState().equals(Status.PUBLISHED)) {
            throw new ForbiddenException("Нельзя добавить запрос на участие в не опубликованном событии!");
        }

        if (requestRepository.findByEventIdAndStatus(eventId, RequestStatus.CONFIRMED).size() == event.getParticipantLimit()) {
            throw new ForbiddenException("У события достигнут лимит запросов на участие");
        }

        Request request = Request.builder()
                .created(LocalDateTime.now())
                .event(event)
                .requester(user)
                .status(!event.isRequestModeration() ? RequestStatus.CONFIRMED : RequestStatus.PENDING)
                .build();
        return requestMapper.toDto(requestRepository.save(request));
    }

    @Override
    public ParticipationRequestDto cancelRequest(Long userId,
                                                 Long requestId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не существует!"));
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Заявка не существует"));
        request.setStatus(RequestStatus.CANCELED);
        return requestMapper.toDto(requestRepository.save(request));
    }
}