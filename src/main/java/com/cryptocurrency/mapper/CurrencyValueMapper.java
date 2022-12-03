package com.cryptocurrency.mapper;

import com.cryptocurrency.entity.domain.CurrencyValue;
import com.cryptocurrency.entity.dto.CurrencyValueDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = DesignationMapper.class)
public interface CurrencyValueMapper {

    CurrencyValueDto toDto(CurrencyValue currencyValue);

    CurrencyValue toModal(CurrencyValueDto currencyValueDto);

    List<CurrencyValueDto> toDtoList(List<CurrencyValue> currencyValueList);

    List<CurrencyValue> toModalList(List<CurrencyValueDto> currencyValueDtoList);
}
