package ru.practicum.ewm.controllers.private_api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.entity.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.services.request.RequestService;

import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}/requests")
@Validated
public class PrivateRequestController {
    private final RequestService requestService;

    @GetMapping
    public List<ParticipationRequestDto> getUserRequests(@Positive @PathVariable Long userId) {
        log.info("PrivateAPI RequestController, getUserRequests {}", userId);
        return requestService.findByRequesterId(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto privatePostRequest(@Positive @PathVariable Long userId,
                                                      @Positive @RequestParam Long eventId) {
        log.info("PrivateAPI RequestController, post userId: {}, eventId: {}", userId, eventId);
        return requestService.findByRequesterIdAndEventId(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto privateApiPatchRequest(@Positive @PathVariable Long userId,
                                                          @Positive @PathVariable Long requestId) {
        log.info("PrivateAPI RequestController, patch userId: {}, requestId: {}", userId, requestId);
        return requestService.cancelRequest(userId, requestId);
    }
}
