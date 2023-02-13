package com.cryptocurrency.entity.dto;

import lombok.Data;

@Data
public class AlertDto {
    private final Long id;
    private final CoinDto coin;
    private final Double price;
    private final DesignationDto designation;
}