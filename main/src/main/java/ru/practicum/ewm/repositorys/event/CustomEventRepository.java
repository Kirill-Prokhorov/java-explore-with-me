package ru.practicum.ewm.repositorys.event;

import ru.practicum.ewm.entity.enums.Sort;
import ru.practicum.ewm.entity.enums.Status;
import ru.practicum.ewm.entity.model.Event;

import java.time.LocalDateTime;
import java.util.List;

public interface CustomEventRepository {
    List<Event> adminGetEventsByParams(List<Long> users,
                                       List<Status> states,
                                       List<Long> categories,
                                       LocalDateTime rangeStart,
                                       LocalDateTime rangeEnd,
                                       Integer from,
                                       Integer size);

    List<Event> publicGetEventsByParams(String text,
                                        List<Long> categories,
                                        LocalDateTime rangeStart,
                                        LocalDateTime rangeEnd,
                                        Boolean onlyAvailable,
                                        Sort sort,
                                        Integer from,
                                        Integer size, Status defaultStatus);
}