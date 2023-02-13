package com.cryptocurrency.mapper;

import com.cryptocurrency.entity.domain.PriceAlerts;
import com.cryptocurrency.entity.dto.PriceAlertsDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class, AlertMapper.class})
public interface PriceAlertsMapper {

    PriceAlertsDto toDto(PriceAlerts priceAlerts);

    PriceAlerts toModal(PriceAlertsDto priceAlertsDto);

    List<PriceAlertsDto> toDtoList(List<PriceAlerts> priceAlertsList);

    List<PriceAlerts> toModalList(List<PriceAlertsDto> priceAlertsDtoList);
}
