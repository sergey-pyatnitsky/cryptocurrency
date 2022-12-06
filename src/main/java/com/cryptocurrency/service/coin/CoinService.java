package com.cryptocurrency.service.coin;

import com.cryptocurrency.entity.domain.CoinMarket;
import com.cryptocurrency.entity.dto.gesko.GeskoCoinMarketDto;

import java.util.List;

public interface CoinService {

    List<String> findCoinsId();

    List<CoinMarket> persistGeskoInfo(List<GeskoCoinMarketDto> geskoCoinMarketDtoList, String currency);

}
