package com.cryptocurrency.entity.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CurrencyValueDto {
    private final Long id;
    private final DesignationDto designation;
    private final Long value;
}