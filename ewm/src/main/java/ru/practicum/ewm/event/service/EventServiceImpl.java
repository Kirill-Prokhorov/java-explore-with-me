package ru.practicum.ewm.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.Sort;
import ru.practicum.ewm.event.model.State;
import ru.practicum.ewm.event.model.StateAction;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.*;
import ru.practicum.ewm.request.dto.RequestDto;
import ru.practicum.ewm.request.dto.RequestMapper;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;
import ru.practicum.ewm.category.repository.CategoryRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    @Override
    public EventDto createEvent(Long userId, EventCreationDto eventDto) {
        log.info("Запрос на создание события");
        if (LocalDateTime.now().plusHours(2).isAfter(eventDto.getEventDate())) {
            throw new ValidationException("Не верная дата начала события");
        }
        return EventMapper.toDto(eventRepository.save(EventMapper.toClass(eventDto, userId)));
    }

    @Transactional
    @Override
    public EventDto adminUpdate(Long eventId, EventCreationDto eventDto) {
        log.info("Запрос на обновление события от администратора");
        Event event = updateAdminProcedure(retrieveEvent(eventId), eventDto);
        return EventMapper.toDto(eventRepository.save(event));
    }

    @Override
    public List<EventCutDto> getPublicEvents(String text, List<Long> categoriesIds, LocalDateTime neededStart,
                                             LocalDateTime neededEnd, Boolean isPaid, Boolean available,
                                             Sort sort, Integer index, Integer size) {
        log.info("Запрос на получение public событий");
        Pageable pageable = PageRequest.of(index / size, size);
        if (neededStart == null) {
            neededStart = LocalDateTime.now();
        }
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }
        List<EventCutDto> eventCutDtoList = eventRepository.getPublicEvents(text, categoriesIds, neededStart,
                        neededEnd, isPaid, pageable).stream()
                .map(EventMapper::toCutDto)
                .collect(Collectors.toList());
        if (available) {
            eventCutDtoList = eventCutDtoList.stream()
                    .filter(eventCutDto -> participationValidation(retrieveEvent(eventCutDto.getId())))
                    .collect(Collectors.toList());
        }
        if (sort != null) {
            switch (sort) {
                case VIEWS:
                    eventCutDtoList = eventCutDtoList.stream()
                            .sorted(Comparator.comparingInt(EventCutDto::getViews))
                            .collect(Collectors.toList());
                    break;
                case EVENT_DATE:
                    eventCutDtoList = eventCutDtoList.stream()
                            .sorted(Comparator.comparing(EventCutDto::getEventDate))
                            .collect(Collectors.toList());
                    break;
            }
        }
        return eventCutDtoList.stream()
                .skip(index)
                .limit(size)
                .collect(Collectors.toList());
    }

    @Override
    public EventDto getFullPublicEvent(Long eventId) {
        log.info("Запрос на получение события для не авторизованных пользователей");
        retrieveEvent(eventId);
        log.info("Выдача события");
        Event event = eventRepository.findByIdAndState(eventId, State.PUBLISHED);
        return EventMapper.toDto(event);
    }

    @Override
    public List<EventDto> getAuthorsEvents(Long userId, Integer index, Integer size) {
        log.info("Запрос на получение событий организатора");
        Pageable pageable = PageRequest.of(index / size, size);
        return eventRepository.findAllByInitiatorId(userId, pageable).stream()
                .map(EventMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public EventDto updateOwnersEvent(Long userId, EventUpdateDto eventDto) {
        log.info("Запрос на обновление события от организатора");
        if (LocalDateTime.now().plusHours(2).isAfter(eventDto.getEventDate())) {
            throw new ValidationException("Не верная дата старта");
        }
        Event event = retrieveEvent(eventDto.getEventId());
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        if (!event.getInitiator().getId().equals(userId)) {
            throw new ValidationException("У этого пользователя нет доступа");
        }
        if (event.getState().equals(State.REJECTED) || event.getState().equals(State.PUBLISHED)) {
            throw new ValidationException("Нельзя обновить событие в этом статусе");
        }
        log.info("Обновляем");
        if (event.getState().equals(State.CANCELED)) {
            event.setState(State.PENDING);
        }
        return EventMapper.toDto(eventRepository.save(updateUserProcedure(event, eventDto)));
    }

    @Override
    public EventDto getAuthorsEventById(Long userId, Long eventId) {
        log.info("Запрос информации о событии организатором");
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId);
        if (event != null) {
            return EventMapper.toDto(event);
        } else {
            throw new NotFoundException("Не верный id пользователя/события");
        }
    }

    @Transactional
    @Override
    public EventDto cancelAuthorsEvent(Long userId, Long eventId) {
        log.info("Запрос на отмену события организатором");
        Event event = retrieveEvent(eventId);
        if (!event.getInitiator().getId().equals(userId)) {
            throw new ValidationException("У пользователя нет прав");
        }
        if (!event.getState().equals(State.PENDING)) {
            throw new ValidationException("Невозможно отменить событие которое не ожидается");
        }
        event.setState(State.CANCELED);
        return EventMapper.toDto(eventRepository.save(event));
    }

    @Override
    public List<EventDto> getAdminEvents(List<Long> users, List<State> states, List<Long> categories,
                                         LocalDateTime neededStart, LocalDateTime neededEnd, Integer index,
                                         Integer size) {
        log.info("Запрос событий администратором");
        Pageable pageable = PageRequest.of(index / size, size);
        if (neededStart == null) {
            neededStart = LocalDateTime.now();
        }
        return eventRepository.getAdminEvents(users, states, categories, neededStart, neededEnd, pageable).stream()
                .map(EventMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public EventDto updateEvent (Long eventId, EventUpdateDto eventDto) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено!"));

        if (eventDto.getEventDate() != null && eventDto.getEventDate().isBefore(LocalDateTime.now())) {
            throw new AlreadyExistsException("Нельзя изменить дату события на прошедшее время!");
        }

        if (eventDto.getEventDate() != null && eventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new AlreadyExistsException("Дата события не может быть раньше 2 часов от текущего времени!");
        }

        if (!event.getState().equals(State.PENDING)) {
            throw new AlreadyExistsException("Событие не удовлетворяет правилам редактирования по статусу");
        }

        if (eventDto.getCategory() != null)
            event.setCategory(categoryRepository.findById(eventDto.getCategory())
                    .orElseThrow(() -> new NotFoundException("Категория не существует!")));

        if (eventDto.getDescription() != null)
            event.setDescription(eventDto.getDescription());
        if (eventDto.getAnnotation() != null)
            event.setAnnotation(eventDto.getAnnotation());
        if (eventDto.getEventDate() != null)
            event.setEventDate(eventDto.getEventDate());
        if (eventDto.getLocation() != null) {
            event.setLon(eventDto.getLocation().getLon());
            event.setLat(eventDto.getLocation().getLat());
        }
        if (eventDto.getPaid() != null)
            event.setPaid(eventDto.getPaid());
        if (eventDto.getParticipantLimit() != null)
            event.setParticipantLimit(eventDto.getParticipantLimit());
        if (eventDto.getTitle() != null)
            event.setTitle(eventDto.getTitle());

        if (eventDto.getStateAction() != null) {
            if (eventDto.getStateAction().equals(StateAction.PUBLISH_EVENT) && event.getState().equals(State.PENDING)) {
                event.setPublished(LocalDateTime.now());
                event.setState(State.PUBLISHED);
            } else if (eventDto.getStateAction().equals(StateAction.REJECT_EVENT) && !event.getState().equals(State.PUBLISHED)) {
                event.setState(State.CANCELED);
            } else
                throw new AlreadyExistsException("Событие не удовлетворяет правилам редактирования по статусу");
        }
        return EventMapper.toDto(eventRepository.save(event));
    }
    /*public EventDto updateEvent(Long eventId) {
        log.info("Запрос на публикацию");
        Event event = retrieveEvent(eventId);
        if (LocalDateTime.now().plusHours(1).isAfter(event.getEventDate()) ||
                !event.getState().equals(State.PENDING)) {
            throw new ValidationException("Публикация невозможна");
        }
        log.info("Публикация");
        event.setState(State.PUBLISHED);
        event.setPublished(LocalDateTime.now());
        return EventMapper.toDto(eventRepository.save(event));
    }*/

    @Transactional
    @Override
    public EventDto rejectEvent(Long eventId) {
        log.info("Запрос на отклонение события");
        Event event = retrieveEvent(eventId);
        if (event.getState().equals(State.PUBLISHED)) {
            throw new ValidationException("Событие уже опубликовано");
        }
        log.info("Отклонение");
        event.setState(State.CANCELED);
        return EventMapper.toDto(eventRepository.save(event));
    }

    @Override
    public List<RequestDto> getCurrentEventsUsersRequests(Long userId, Long eventId) {
        log.info("Запрос на получение запросов событий");
        Event event = retrieveEvent(eventId);
        if (!event.getInitiator().getId().equals(userId)) {
            throw new ValidationException("У пользователя нет прав");
        }
        return requestRepository.findAllByEventId(eventId).stream()
                .map(RequestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public RequestDto confirmUsersRequest(Long userId, Long eventId, Long requestId) {
        log.info("Запрос на получение запроса события");
        Event event = retrieveEvent(eventId);
        isUpdateValid(event, userId, requestId);
        if (participationValidation(event)) {
            throw new ValidationException("Достигнут лимит");
        }
        log.info("Подтверждение");
        Request request = requestRepository.getReferenceById(requestId);
        request.setState(ru.practicum.ewm.request.model.State.CONFIRMED);
        if (participationValidation(event)) {
            event.getRequests().removeIf(event2 ->
                    !event2.getState().equals(ru.practicum.ewm.request.model.State.CONFIRMED));
        }
        eventRepository.save(event);
        return RequestMapper.toDto(request);
    }

    @Transactional
    @Override
    public RequestDto rejectUsersRequest(Long userId, Long eventId, Long requestId) {
        log.info("Запрос на отклонение запроса событий");
        Event event = retrieveEvent(eventId);
        if (!userId.equals(event.getInitiator().getId())) {
            throw new ValidationException("Пользователь не можетутвердить/отклонить запрос");
        }
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос не найден"));
        log.info("Отклонение");
        request.setState(ru.practicum.ewm.request.model.State.REJECTED);
        eventRepository.save(event);
        return RequestMapper.toDto(request);
    }

    private Event retrieveEvent(Long id) {
        return eventRepository.findById(id).orElseThrow(() -> new EventNotFoundException("Событие не найдено"));
    }

    private Event updateAdminProcedure(Event event, EventCreationDto newEvent) {
        baseUpdateProcedure(event, newEvent.getAnnotation(), newEvent.getCategory(), newEvent.getDescription(),
                newEvent.getEventDate(), newEvent.getPaid(), newEvent.getParticipantLimit(), newEvent.getTitle());
        if (newEvent.getRequestModeration() != null) {
            event.setModeration(newEvent.getRequestModeration());
        }
        if (newEvent.getLocation() != null) {
            event.setLat(newEvent.getLocation().getLat());
            event.setLon(newEvent.getLocation().getLon());
        }
        return event;
    }

    private void baseUpdateProcedure(Event event, String annotation, Long category, String description,
                                     LocalDateTime eventDate, Boolean paid, Integer participantLimit,
                                     String title) {
        if (annotation != null) {
            event.setAnnotation(annotation);
        }
        if (category != null) {
            event.setCategory(Category.builder().id(category).build());
        }
        if (description != null) {
            event.setDescription(description);
        }
        if (eventDate != null) {
            event.setEventDate(eventDate);
        }
        if (paid != null) {
            event.setPaid(paid);
        }
        if (participantLimit != null) {
            event.setParticipantLimit(participantLimit);
        }
        if (title != null) {
            event.setTitle(title);
        }
    }

    private Event updateUserProcedure(Event event, EventUpdateDto newEvent) {
        baseUpdateProcedure(event, newEvent.getAnnotation(), newEvent.getCategory(), newEvent.getDescription(),
                newEvent.getEventDate(), newEvent.getPaid(), newEvent.getParticipantLimit(), newEvent.getTitle());
        return event;
    }

    private void isUpdateValid(Event event, Long userId, Long requestId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String
                        .format("Пользователь с id %d отсутствует", userId)));
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(String
                        .format("Запрос с id %d отсутствует", requestId)));
        if (!request.getEvent().getId().equals(event.getId())) {
            throw new ValidationException("Wrong event/request link");
        }
        if (!request.getState().equals(ru.practicum.ewm.request.model.State.PENDING)) {
            throw new ValidationException("Status is not Pending");
        }
        if (!user.getId().equals(event.getInitiator().getId())) {
            throw new ValidationException("Wrong user can't approve/reject");
        }
        if (!event.getModeration() || (event.getParticipantLimit() == 0)) {
            throw new ValidationException("No need to confirm the request");
        }
    }

    private boolean participationValidation(Event event) {
        long confirmedParticipants = event.getRequests().stream()
                .filter(event2 -> event2.getState().equals(ru.practicum.ewm.request.model.State.CONFIRMED)).count();
        return confirmedParticipants >= event.getParticipantLimit();
    }

}
