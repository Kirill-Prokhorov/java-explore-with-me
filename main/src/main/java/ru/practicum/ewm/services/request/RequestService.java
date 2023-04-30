package ru.practicum.ewm.services.request;

import ru.practicum.ewm.entity.dto.request.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
    List<ParticipationRequestDto> findByRequesterId(Long userId);

    ParticipationRequestDto findByRequesterIdAndEventId(Long userId, Long eventId);

    ParticipationRequestDto cancelRequest(Long userId, Long requestId);
}
