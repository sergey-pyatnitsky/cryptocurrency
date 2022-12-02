package com.cryptocurrency.mapper;

import com.cryptocurrency.entity.domain.Portfolio;
import com.cryptocurrency.entity.dto.PortfolioDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = PortfolioCoinMapper.class)
public interface PortfolioMapper {

    PortfolioDto toDto(Portfolio portfolio);

    Portfolio toModal(PortfolioDto portfolioDto);

    List<PortfolioDto> toDtoList(List<Portfolio> portfolioList);

    List<Portfolio> toModalList(List<PortfolioDto> portfolioDtoList);
}
