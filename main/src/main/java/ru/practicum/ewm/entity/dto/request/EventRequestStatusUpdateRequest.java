package ru.practicum.ewm.entity.dto.request;

import lombok.*;
import ru.practicum.ewm.entity.enums.RequestStatus;

import java.util.List;

@ToString
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventRequestStatusUpdateRequest {
    private List<Long> requestIds;
    private RequestStatus status;
}
