package ru.practicum.ewm.controllers.private_api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.entity.dto.event.EventFullDto;
import ru.practicum.ewm.entity.dto.event.EventShortDto;
import ru.practicum.ewm.entity.dto.event.NewEventDto;
import ru.practicum.ewm.entity.dto.event.UpdateEventUserRequest;
import ru.practicum.ewm.entity.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.entity.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.ewm.entity.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.services.event.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}/events")
@Validated
public class PrivateEventController {

    private final EventService eventService;

    @GetMapping
    public List<EventShortDto> privateGetEventsByUser(@PathVariable Long userId,
                                                      @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                      @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("PrivateAPI EventController, getByInitiatorId: {}", userId);
        return eventService.getAllByUserIdPrivateAPI(userId, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto privatePostEvent(@PathVariable Long userId,
                                         @Valid @RequestBody NewEventDto eventDto) {
        log.info("PrivateAPI EventController, post userId: {}, eventDto: {}", userId, eventDto.toString());
        return eventService.postPrivateAPI(userId, eventDto);
    }

    @GetMapping("/{eventId}")
    public EventFullDto privateGetEventById(@PathVariable Long userId,
                                            @PathVariable Long eventId) {
        log.info("PrivateAPI EventController, getById userId: {}, eventId: {}", userId, eventId);
        return eventService.findByIdAndInitiatorIdPrivateAPI(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto privatePatchEventByUser(@PathVariable Long userId,
                                                @PathVariable Long eventId,
                                                @Valid @RequestBody UpdateEventUserRequest updateEventUserRequest) {
        log.info("PrivateAPI EventController, patchEventByInitiator userId: {}, eventId: {}, updateEventUserRequest: {}",
                userId, eventId, updateEventUserRequest.toString());
        return eventService.patchPrivateAPI(userId, eventId, updateEventUserRequest);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> privateGetRequests(@PathVariable Long userId,
                                                            @PathVariable Long eventId) {
        log.info("PrivateAPI EventController, getRequests userId: {}, eventId: {}", userId, eventId);
        return eventService.findRequestsByIdAndInitiatorId(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult privatePatchRequests(@PathVariable Long userId,
                                                               @PathVariable Long eventId,
                                                               @Valid @RequestBody EventRequestStatusUpdateRequest updateRequest) {
        log.info("PrivateAPI EventController, patchRequests userId: {}, eventId: {}, updateRequest: {}",
                userId, eventId, updateRequest.toString());
        return eventService.patchRequests(userId, eventId, updateRequest);
    }

    @GetMapping("/subscriptions")
    public List<EventFullDto> getSubscriptionEvents(@PathVariable Long userId) {
        log.info("PrivateAPI EventController, getSubscriptionEvents userId: {}", userId);
        return eventService.findByFollower(userId);
    }
}