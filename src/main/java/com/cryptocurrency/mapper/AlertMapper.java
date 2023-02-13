package com.cryptocurrency.mapper;

import com.cryptocurrency.entity.domain.Alert;
import com.cryptocurrency.entity.dto.AlertDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {CoinMapper.class, DesignationMapper.class})
public interface AlertMapper {

    AlertDto toDto(Alert alert);

    Alert toModal(AlertDto alertDto);

    List<AlertDto> toDtoList(List<Alert> alertList);

    List<Alert> toModalList(List<AlertDto> alertDtoList);
}
