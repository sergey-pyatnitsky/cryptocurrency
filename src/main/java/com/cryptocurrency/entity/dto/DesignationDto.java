package com.cryptocurrency.entity.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DesignationDto {
    private final String name;
    private final String symbol;
}