package com.vorstu.excel.mapper;

import com.vorstu.excel.dto.WorkDayDto;
import com.vorstu.excel.model.WorkDayEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface WorkDayMapper {

    WorkDayMapper MAPPER = Mappers.getMapper(WorkDayMapper.class);

    WorkDayDto toDto(WorkDayEntity source);

}
