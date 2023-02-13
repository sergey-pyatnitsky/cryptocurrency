package com.cryptocurrency.entity.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserCoinInfoDto {
    private boolean isFavorite;
    private boolean isExistPriceAlert;
}
