package com.cryptocurrency.entity.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PriceAlertsDto {
    private final Long id;
    private final List<AlertDto> alerts;
    private final UserDto user;
}