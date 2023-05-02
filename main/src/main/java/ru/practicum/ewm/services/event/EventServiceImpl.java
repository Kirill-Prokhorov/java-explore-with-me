package ru.practicum.ewm.services.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.ViewStatsDTO;
import ru.practicum.ewm.WebClientService;
import ru.practicum.ewm.entity.dto.event.EventFullDto;
import ru.practicum.ewm.entity.dto.event.EventShortDto;
import ru.practicum.ewm.entity.dto.event.NewEventDto;
import ru.practicum.ewm.entity.dto.event.UpdateEventAdminRequest;
import ru.practicum.ewm.entity.dto.event.UpdateEventUserRequest;
import ru.practicum.ewm.entity.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.entity.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.ewm.entity.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.entity.enums.RequestStatus;
import ru.practicum.ewm.entity.enums.Sort;
import ru.practicum.ewm.entity.enums.StateAction;
import ru.practicum.ewm.entity.enums.Status;
import ru.practicum.ewm.entity.mapper.EventMapper;
import ru.practicum.ewm.entity.mapper.RequestMapper;
import ru.practicum.ewm.entity.model.Category;
import ru.practicum.ewm.entity.model.Event;
import ru.practicum.ewm.entity.model.Location;
import ru.practicum.ewm.entity.model.Request;
import ru.practicum.ewm.entity.model.User;
import ru.practicum.ewm.exception.BadInputDataException;
import ru.practicum.ewm.exception.ForbiddenException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.repositorys.CategoryRepository;
import ru.practicum.ewm.repositorys.LocationRepository;
import ru.practicum.ewm.repositorys.RequestRepository;
import ru.practicum.ewm.repositorys.UserRepository;
import ru.practicum.ewm.repositorys.event.EventRepository;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final WebClientService webClientService;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final EventMapper eventMapper;
    private final LocationRepository locationRepository;
    private final RequestRepository requestRepository;
    private final RequestMapper requestMapper;


    @Override
    public List<EventFullDto> getByParamsAdminAPI(List<Long> users,
                                                  List<Status> states,
                                                  List<Long> categories,
                                                  LocalDateTime rangeStart,
                                                  LocalDateTime rangeEnd,
                                                  Integer from,
                                                  Integer size) {

        return eventRepository.adminGetEventsByParams(
                        users,
                        states,
                        categories,
                        rangeStart,
                        rangeEnd,
                        from,
                        size).stream()
                .map(eventMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto patchAdminAPI(Long eventId, UpdateEventAdminRequest eventDto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено!"));

        if (eventDto.getEventDate() != null && eventDto.getEventDate().isBefore(LocalDateTime.now())) {
            throw new ForbiddenException("Нельзя изменить дату события на прошедшее время!");
        }

        if (eventDto.getEventDate() != null && eventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ForbiddenException("Дата события не может быть раньше 2 часов от текущего времени!");
        }

        if (!event.getState().equals(Status.PENDING)) {
            throw new ForbiddenException("Событие не удовлетворяет правилам редактирования по статусу");
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
        if (eventDto.getLocation() != null)
            event.setLocation(locationRepository.save(eventDto.getLocation()));
        if (eventDto.getPaid() != null)
            event.setPaid(eventDto.getPaid());
        if (eventDto.getParticipantLimit() != null)
            event.setParticipantLimit(eventDto.getParticipantLimit());
        if (eventDto.getRequestModeration() != null)
            event.setRequestModeration(eventDto.getRequestModeration());
        if (eventDto.getTitle() != null)
            event.setTitle(eventDto.getTitle());

        if (eventDto.getStateAction() != null) {
            if (eventDto.getStateAction().equals(StateAction.PUBLISH_EVENT) && event.getState().equals(Status.PENDING)) {
                event.setPublishedOn(LocalDateTime.now());
                event.setState(Status.PUBLISHED);
            } else if (eventDto.getStateAction().equals(StateAction.REJECT_EVENT) && !event.getState().equals(Status.PUBLISHED)) {
                event.setState(Status.CANCELED);
            } else
                throw new ForbiddenException("Событие не удовлетворяет правилам редактирования по статусу");
        }
        return eventMapper.toDto(eventRepository.save(event));
    }

    @Override
    public List<EventFullDto> getByParamsPublicAPI(String text,
                                                   List<Long> categories,
                                                   Boolean paid,
                                                   LocalDateTime rangeStart,
                                                   LocalDateTime rangeEnd,
                                                   Boolean onlyAvailable,
                                                   Sort sort,
                                                   Integer from,
                                                   Integer size,
                                                   HttpServletRequest request) {

        webClientService.postHit(
                "ewm-main-service",
                request.getRequestURI(),
                request.getRemoteAddr(),
                LocalDateTime.now()
        );

        Status defaultStatus = Status.PUBLISHED;

        if (rangeStart == null) {
            rangeStart = LocalDateTime.now().minusYears(10);
        }

        if (rangeEnd == null) {
            rangeEnd = LocalDateTime.now().plusYears(10);
        }

        List<Event> events = eventRepository.publicGetEventsByParams(
                text,
                categories,
                rangeStart,
                rangeEnd,
                onlyAvailable,
                sort,
                from,
                size,
                defaultStatus);


        List<EventFullDto> eventFullDtos = events.stream()
                .map(eventMapper::toDto)
                .collect(Collectors.toList());

        getViewsForEventFullDtos(rangeStart, rangeEnd, eventFullDtos);
        getConfirmedRequestsForEventFullDtos(eventFullDtos);

        if (sort != null && sort.equals(Sort.VIEWS)) {
            eventFullDtos.sort(Comparator.comparing(EventFullDto::getViews).reversed());
        }
        return eventFullDtos;
    }

    @Override
    public EventFullDto getByIdPublicAPI(Long eventId, HttpServletRequest request) {
        webClientService.postHit(
                "ewm-main-service",
                request.getRequestURI(),
                request.getRemoteAddr(),
                LocalDateTime.now()
        );
        Event event = eventRepository.findByIdAndState(eventId, Status.PUBLISHED)
                .orElseThrow(() -> new NotFoundException("Событие не найдено!"));
        EventFullDto eventFullDto = eventMapper.toDto(event);

        getViewsForEventFullDtos(null, null, Collections.singletonList(eventFullDto));
        getConfirmedRequestsForEventFullDtos(Collections.singletonList(eventFullDto));
        return eventFullDto;
    }

    @Override
    public List<EventShortDto> getAllByUserIdPrivateAPI(Long userId, Integer from, Integer size) {

        Pageable pageable = PageRequest.of(from, size);
        return eventRepository.findByInitiatorId(userId, pageable).stream()
                .map(eventMapper::toShortDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto postPrivateAPI(Long userId, NewEventDto eventDto) {
        Category category = categoryRepository.findById(eventDto.getCategory())
                .orElseThrow(() -> new NotFoundException("Категория не существует!"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не существует!"));

        if (eventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ForbiddenException("Намеченное событие не может быть раньше, чем через два часа от текущего момента");
        }

        Event event = eventMapper.fromNewDto(eventDto);
        Location location = locationRepository.save(eventDto.getLocation());

        event.setCategory(category);
        event.setCreatedOn(LocalDateTime.now());
        event.setInitiator(user);
        event.setLocation(location);
        event.setState(Status.PENDING);

        return eventMapper.toDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto findByIdAndInitiatorIdPrivateAPI(Long userId, Long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не существует!"));
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено!"));
        EventFullDto dto = eventMapper.toDto(event);
        getConfirmedRequestsAndViews(dto);
        return dto;
    }

    @Override
    public EventFullDto patchPrivateAPI(Long userId, Long eventId, UpdateEventUserRequest eventUserRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не существует!"));
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено!"));

        if (eventUserRequest.getEventDate() != null && eventUserRequest.getEventDate().isBefore(LocalDateTime.now())) {
            throw new ForbiddenException("Нельзя изменить дату события на прошедшее время!");
        }

        if (event.getState().equals(Status.PUBLISHED)) {
            throw new ForbiddenException("Событие не удовлетворяет правилам редактирования по статусу");
        }

        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new BadInputDataException("Намеченное событие не может быть раньше, чем через два часа от текущего момента");
        }

        if (eventUserRequest.getCategory() != null)
            event.setCategory(categoryRepository.findById(eventUserRequest.getCategory())
                    .orElseThrow(() -> new NotFoundException("Категория не существует!")));

        if (eventUserRequest.getDescription() != null)
            event.setDescription(eventUserRequest.getDescription());
        if (eventUserRequest.getAnnotation() != null)
            event.setAnnotation(eventUserRequest.getAnnotation());
        if (eventUserRequest.getEventDate() != null)
            event.setEventDate(eventUserRequest.getEventDate());
        if (eventUserRequest.getLocation() != null)
            event.setLocation(locationRepository.save(eventUserRequest.getLocation()));
        if (eventUserRequest.getPaid() != null)
            event.setPaid(eventUserRequest.getPaid());
        if (eventUserRequest.getParticipantLimit() != null)
            event.setParticipantLimit(event.getParticipantLimit());
        if (eventUserRequest.getRequestModeration() != null)
            event.setRequestModeration(eventUserRequest.getRequestModeration());
        if (eventUserRequest.getTitle() != null)
            event.setTitle(eventUserRequest.getTitle());

        if (eventUserRequest.getStateAction() != null) {
            if (eventUserRequest.getStateAction().equals(StateAction.CANCEL_REVIEW)) {
                event.setState(Status.CANCELED);
            } else if
            (eventUserRequest.getStateAction().equals(StateAction.SEND_TO_REVIEW)) {
                event.setState(Status.PENDING);
            }
        }
        return eventMapper.toDto(eventRepository.save(event));
    }

    @Override
    public List<ParticipationRequestDto> findRequestsByIdAndInitiatorId(Long userId, Long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не существует!"));
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено!"));
        return requestRepository.findByEventId(eventId).stream()
                .map(requestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventRequestStatusUpdateResult patchRequests(Long userId,
                                                        Long eventId,
                                                        EventRequestStatusUpdateRequest updateRequest) {
        log.info("{},{},{}", userId, eventId, updateRequest.toString());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не существует!"));
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено0!"));
        List<Request> confirmed = new ArrayList<>();
        List<Request> rejected = new ArrayList<>();

        int updateAdditionalLimit = 0;
        if (updateRequest.getStatus().equals(RequestStatus.CONFIRMED)) {
            updateAdditionalLimit = updateRequest.getRequestIds().size();
        }

        List<Request> currentConfirmedRequests = requestRepository.findByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);

        if ((currentConfirmedRequests.size() + updateAdditionalLimit) > event.getParticipantLimit()) {
            throw new ForbiddenException("Достигнут лимит по заявкам на данное событие!");
        }

        List<Request> requestsToUpdate = requestRepository.findByIdIn(updateRequest.getRequestIds());

        if (requestsToUpdate.stream().anyMatch(request -> !request.getStatus().equals(RequestStatus.PENDING)))
            throw new ForbiddenException("Cтатус можно изменить только у заявок, находящихся в состоянии ожидания!");

        requestsToUpdate.forEach(request -> request.setStatus(updateRequest.getStatus()));

        List<Request> requestsUpdated = requestRepository.saveAll(requestsToUpdate);

        if ((currentConfirmedRequests.size() + updateAdditionalLimit) == event.getParticipantLimit()) {
            List<Request> requestsToReject = requestRepository.findByEventIdAndStatus(eventId, RequestStatus.PENDING);
            requestsToReject.forEach(request -> request.setStatus(RequestStatus.REJECTED));
            rejected.addAll(requestRepository.saveAll(requestsToReject));
        }

        requestsUpdated.forEach(request -> {
            if (request.getStatus().equals(RequestStatus.CONFIRMED)) {
                confirmed.add(request);
            } else if (request.getStatus().equals(RequestStatus.REJECTED)) {
                rejected.add(request);
            }
        });


        return EventRequestStatusUpdateResult.builder()
                .confirmedRequests(confirmed.stream()
                        .map(requestMapper::toDto)
                        .collect(Collectors.toList()))
                .rejectedRequests(rejected.stream()
                        .map(requestMapper::toDto)
                        .collect(Collectors.toList()))
                .build();
    }


    private void getViewsForEventFullDtos(LocalDateTime start,
                                          LocalDateTime end,
                                          List<EventFullDto> eventFullDtos) {
        boolean isUniqueIp = false;
        if (start == null) {
            start = LocalDateTime.now().minusYears(100);
        }
        if (end == null) {
            end = LocalDateTime.now().plusYears(100);
        }

        List<String> uris = eventFullDtos.stream()
                .map(eventFullDto -> "/events/" + eventFullDto.getId())
                .collect(Collectors.toList());
        log.debug("{},{},{}", start, end, uris);
        List<ViewStatsDTO> statsDTOList = webClientService.getStats(start, end, uris, isUniqueIp);

        Map<Long, Long> mapOfViews = statsDTOList.stream()
                .collect(Collectors.toMap(
                        viewStatsDTO ->
                                Long.parseLong(viewStatsDTO.getUri().replace("/events/", "")),
                        ViewStatsDTO::getHits));

        eventFullDtos.forEach(eventFullDto ->
                eventFullDto.setViews(mapOfViews.getOrDefault(eventFullDto.getId(), 0L)));

        log.debug("{}", mapOfViews);
    }

    private void getConfirmedRequestsForEventFullDtos(List<EventFullDto> eventFullDtos) {
        List<Long> eventsIds = eventFullDtos.stream()
                .map(EventFullDto::getId)
                .collect(Collectors.toList());

        List<Request> requests = requestRepository.findByEventIdInAndStatus(eventsIds, RequestStatus.CONFIRMED);

        Map<Long, Long> mapOfRequests = requests.stream()
                .collect(
                        Collectors.groupingBy(request -> request.getEvent().getId(),
                                Collectors.counting())
                );

        eventFullDtos.forEach(eventFullDto ->
                eventFullDto.setViews(mapOfRequests.getOrDefault(eventFullDto.getId(), 0L)));
    }

    private void getConfirmedRequestsAndViews(EventFullDto eventFullDto) {
        LocalDateTime start = LocalDateTime.now().minusYears(100);
        LocalDateTime end = LocalDateTime.now().plusYears(100);
        String uri = "/events/" + eventFullDto.getId();
        boolean isUniqueIp = false;

        List<ViewStatsDTO> statsDTOList = webClientService.getStats(start, end, Collections.singletonList(uri), isUniqueIp);

        if (!statsDTOList.isEmpty()) {
            ViewStatsDTO viewStatsDTO = statsDTOList.get(0);
            eventFullDto.setViews(viewStatsDTO.getHits());
        }

        List<Request> requests = requestRepository.findByEventIdAndStatus(eventFullDto.getId(), RequestStatus.CONFIRMED);

        eventFullDto.setConfirmedRequests(requests.size());
    }

    @Override
    public List<EventFullDto> findByFollower(Long followerId) {
        User user = userRepository.findById(followerId)
                .orElseThrow(() -> new NotFoundException("Пользователь не существует!"));

        List<Event> events = eventRepository.getEventsBySubscriptionsCustom(
                followerId,
                Status.PUBLISHED,
                LocalDateTime.now()
        );

        List<EventFullDto> eventFullDtos = events.stream()
                .map(eventMapper::toDto)
                .collect(Collectors.toList());

        getConfirmedRequestsForEventFullDtos(eventFullDtos);
        getViewsForEventFullDtos(null, null, eventFullDtos);

        return eventFullDtos;
    }
}