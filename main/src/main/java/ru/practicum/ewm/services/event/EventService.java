package ru.practicum.ewm.services.event;

import ru.practicum.ewm.entity.dto.event.*;
import ru.practicum.ewm.entity.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.entity.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.ewm.entity.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.entity.enums.Sort;
import ru.practicum.ewm.entity.enums.Status;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    List<EventFullDto> getByParamsAdminAPI(List<Long> users,
                                           List<Status> states,
                                           List<Long> categories,
                                           LocalDateTime rangeStart,
                                           LocalDateTime rangeEnd,
                                           Integer from,
                                           Integer size);

    EventFullDto patchAdminAPI(Long eventId, UpdateEventAdminRequest eventDto);

    List<EventFullDto> getByParamsPublicAPI(String text, List<Long> categories,
                                            Boolean paid, LocalDateTime rangeStart,
                                            LocalDateTime rangeEnd, Boolean onlyAvailable, Sort sort,
                                            Integer from, Integer size, HttpServletRequest request);

    EventFullDto getByIdPublicAPI(Long eventId, HttpServletRequest request);

    List<EventShortDto> getAllByUserIdPrivateAPI(Long userId, Integer from, Integer size);

    EventFullDto postPrivateAPI(Long userId, NewEventDto eventDto);

    EventFullDto findByIdAndInitiatorIdPrivateAPI(Long userId, Long eventId);

    EventFullDto patchPrivateAPI(Long userId, Long eventId, UpdateEventUserRequest eventFullDto);

    List<ParticipationRequestDto> findRequestsByIdAndInitiatorId(Long userId, Long eventId);

    EventRequestStatusUpdateResult patchRequests(Long userId, Long eventId,
                                                 EventRequestStatusUpdateRequest updateRequest);

    List<EventFullDto> findByFollower(Long userId);
}
