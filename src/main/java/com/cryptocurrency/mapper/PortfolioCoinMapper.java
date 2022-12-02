package com.cryptocurrency.mapper;

import com.cryptocurrency.entity.domain.PortfolioCoin;
import com.cryptocurrency.entity.dto.PortfolioCoinDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {CoinMapper.class, DesignationMapper.class})
public interface PortfolioCoinMapper {

    PortfolioCoinDto toDto(PortfolioCoin portfolioCoin);

    @Mapping(ignore = true, target = "portfolio")
    PortfolioCoin toModal(PortfolioCoinDto portfolioCoinDto);

    List<PortfolioCoinDto> toDtoList(List<PortfolioCoin> portfolioCoinList);

    List<PortfolioCoin> toModalList(List<PortfolioCoinDto> portfolioCoinDtoList);
}
