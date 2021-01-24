package com.vorstu.excel.mapper;

import com.vorstu.excel.dto.GroupDto;
import com.vorstu.excel.model.GroupEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface GroupMapper {

    GroupMapper MAPPER = Mappers.getMapper(GroupMapper.class);

    GroupDto toDto(GroupEntity source);

    List<GroupDto> toDtoList(List<GroupEntity> source);

}
