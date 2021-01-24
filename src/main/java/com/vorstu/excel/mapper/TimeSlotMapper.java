package com.vorstu.excel.mapper;

import com.vorstu.excel.dto.TimeSlotDto;
import com.vorstu.excel.model.TimeSlotEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface TimeSlotMapper {

    TimeSlotMapper MAPPER = Mappers.getMapper(TimeSlotMapper.class);

    TimeSlotDto toDto(TimeSlotEntity source);

    List<TimeSlotDto> toDtoList(List<TimeSlotEntity> source);

}
