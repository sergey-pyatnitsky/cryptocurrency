package com.cryptocurrency.service.coin;

import com.cryptocurrency.entity.domain.Coin;
import com.cryptocurrency.entity.domain.FavoriteCoin;
import com.cryptocurrency.entity.domain.PriceAlerts;
import com.cryptocurrency.entity.dto.UserCoinInfoDto;
import com.cryptocurrency.entity.dto.gesko.GeskoCoinMarketDto;

import java.util.List;

public interface CoinService {

    List<String> findCoinsId();
    List<String> findCoinsIdBySearch(String search);
    Coin findCoin(String id, String currency);
    List<Coin> findTrendingCoins(String currency);

    FavoriteCoin addFavoriteCoin(String coinId, String username);
    List<Coin> getFavoriteCoinsList(String username, String currency);

    PriceAlerts findByUsername(String username);
    PriceAlerts addPriceAlert(String coinId, Double price, String username, String currency);
    PriceAlerts editPriceAlert(String coinId, Double price, String username);
    boolean removePriceAlert(String coinId, String username);

    void sendPriceAlerts();
    UserCoinInfoDto getUserCoinInfo(String coinId, String username);

    List<Coin> persistGeskoInfo(List<GeskoCoinMarketDto> geskoCoinMarketDtoList, String currency);

    List<String> getDesignationNames();

}
