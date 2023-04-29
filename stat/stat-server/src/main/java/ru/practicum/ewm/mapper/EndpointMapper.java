package ru.practicum.ewm.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.practicum.ewm.EndpointHitDTO;
import ru.practicum.ewm.ViewStatsDTO;
import ru.practicum.ewm.model.EndpointHit;

@Mapper
public interface EndpointMapper {

    EndpointMapper ENDPOINT_MAPPER = Mappers.getMapper(EndpointMapper.class);

    EndpointHit fromDto(EndpointHitDTO endpointHitDTO);

    //ViewStatsDTO toViewDto(EndpointHit endpointHit);

    EndpointHitDTO toEndpointHitDto(EndpointHit endpointHit);
}
