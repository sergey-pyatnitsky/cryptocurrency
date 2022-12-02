package com.cryptocurrency.mapper;

import com.cryptocurrency.entity.domain.Designation;
import com.cryptocurrency.entity.dto.DesignationDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DesignationMapper {

    DesignationDto toDto(Designation designation);

    Designation toModal(DesignationDto designationDto);

    List<DesignationDto> toDtoList(List<Designation> designationList);

    List<Designation> toModalList(List<DesignationDto> designationDtoList);
}
