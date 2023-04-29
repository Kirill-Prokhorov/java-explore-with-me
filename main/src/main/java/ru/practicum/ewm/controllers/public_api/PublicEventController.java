package ru.practicum.ewm.controllers.public_api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.entity.dto.event.EventFullDto;
import ru.practicum.ewm.entity.enums.Sort;
import ru.practicum.ewm.services.event.EventService;
import ru.practicum.ewm.utils.DateTimePattern;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/events")
@Validated
public class PublicEventController {

    private final EventService eventService;

    @GetMapping
    public List<EventFullDto> publicGetByParams(@RequestParam(required = false) String text,
                                                @RequestParam(required = false) List<Long> categories,
                                                @RequestParam(required = false) Boolean paid,
                                                @RequestParam(required = false) @DateTimeFormat(pattern = DateTimePattern.TIME_PATTERN)
                                                LocalDateTime rangeStart,
                                                @RequestParam(required = false) @DateTimeFormat(pattern = DateTimePattern.TIME_PATTERN)
                                                LocalDateTime rangeEnd,
                                                @RequestParam(defaultValue = "false") boolean onlyAvailable,
                                                @RequestParam(required = false) Sort sort,
                                                @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                @Positive @RequestParam(defaultValue = "10") Integer size,
                                                HttpServletRequest request) {
        log.info("PublicAPI EventController, getByParams, text: {}, categories: {}, paid: {}, rangeStart: {}," +
                        "rangeEnd: {}, onlyAvailable: {}, sort: {}, from: {}, size: {}, request: {}",
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size, request.toString());
        return eventService.getByParamsPublicAPI(text, categories, paid, rangeStart,
                rangeEnd, onlyAvailable, sort, from, size, request);
    }

    @GetMapping("/{eventId}")
    public EventFullDto publicGetEvent(@Positive @PathVariable Long eventId,
                                       HttpServletRequest request) {
        log.info("PublicAPI EventController, getById, eventId: {}", eventId);
        return eventService.getByIdPublicAPI(eventId, request);
    }
}
