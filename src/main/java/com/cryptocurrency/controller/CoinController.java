package com.cryptocurrency.controller;

import com.cryptocurrency.entity.dto.CoinDto;
import com.cryptocurrency.entity.dto.PriceAlertsDto;
import com.cryptocurrency.entity.dto.UserCoinInfoDto;
import com.cryptocurrency.entity.dto.gesko.GeskoCoinMarketDto;
import com.cryptocurrency.entity.dto.gesko.OhlcDto;
import com.cryptocurrency.entity.dto.markovits.MarkovitsDto;
import com.cryptocurrency.entity.dto.markovits.PartsOfStock;
import com.cryptocurrency.mapper.CoinMapper;
import com.cryptocurrency.mapper.PriceAlertsMapper;
import com.cryptocurrency.service.coin.CoinService;
import com.cryptocurrency.service.markovits.MarkovitsaServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
public class CoinController {

    @Autowired
    private CoinService coinService;

    @Autowired
    private CoinMapper coinMapper;

    @Autowired
    private PriceAlertsMapper priceAlertsMapper;

    @Autowired
    private MarkovitsaServiceImpl markovitsaService;

    @GetMapping("/public/coin/getCoinsList/{currency}")
    public List<CoinDto> getCoinsList(@PathVariable("currency") String currency, @RequestParam("search") String search) {
        String url = "https://api.coingecko.com/api/v3/coins/markets?vs_currency="
                + currency + "&ids=" + String.join(",", coinService.findCoinsIdBySearch(search))
                + "&order=market_cap_desc&per_page=100&page=1&sparkline=false";

        return coinMapper.toDtoList(
                coinService.persistGeskoInfo(this.getResponseToGescoCoinObject(url), currency)
        );
    }

    @GetMapping("/public/coin/{id}/{currency}")
    public CoinDto getSingleCoin(@PathVariable("id") String id, @PathVariable("currency") String currency) {
        return coinMapper.toDto(coinService.findCoin(id, currency));
    }

    @GetMapping("/public/coin/getTrendingCoins/{currency}")
    public List<CoinDto> getTrendingCoins(@PathVariable("currency") String currency) {
        return coinMapper.toDtoList(coinService.findTrendingCoins(currency));
    }

    @PostMapping("/coin/markovits/analysis/{currency}")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody MarkovitsDto getMarkovitsAnalysis(@RequestParam("coins") String[] coinsId,
                                                           @PathVariable("currency") String currency,
                                                           @RequestBody List<PartsOfStock> partsOfStockList) {
        Map<String, List<OhlcDto>> coinsClosePrice = new HashMap<>();

        for (String coin : coinsId) {
            String url = "https://api.coingecko.com/api/v3/coins/" + coin + "/ohlc?vs_currency=" + currency + "&days=365";
            coinsClosePrice.put(coin, this.getResponseToOhlcObject(url));
        }

        return markovitsaService.getMarkovitsAnalysis(coinsClosePrice, partsOfStockList);
    }

    @PostMapping("/coin/favorite/add")
    @ResponseStatus(HttpStatus.OK)
    public void addFavoriteCoin(@RequestParam("coin_id") String coinId, @RequestParam("username") String username) {
        coinService.addFavoriteCoin(coinId, username);
    }

    @GetMapping("/coin/favorite/all")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody List<CoinDto> getFavoriteCoinByUsernameAndCurrency(
            @RequestParam("username") String username, @RequestParam("currency") String currency) {
        return coinMapper.toDtoList(coinService.getFavoriteCoinsList(username, currency));
    }

    @GetMapping("/coin/price_alerts/all")
    @ResponseStatus(HttpStatus.OK)
    public PriceAlertsDto findPriceAlertByUsername(@RequestParam("username") String username) {
        return priceAlertsMapper.toDto(coinService.findByUsername(username));
    }

    @PostMapping("/coin/price_alerts/add")
    @ResponseStatus(HttpStatus.OK)
    public void addPriceAlert(@RequestParam("coin_id") String coinId, @RequestParam("price") Double price,
                              @RequestParam("username") String username, @RequestParam("currency") String currency) {
        coinService.addPriceAlert(coinId, price, username, currency);
    }

    @PostMapping("/coin/price_alerts/edit")
    @ResponseStatus(HttpStatus.OK)
    public void editPriceAlert(@RequestParam("coin_id") String coinId, @RequestParam("price") Double price,
                              @RequestParam("username") String username) {
        coinService.editPriceAlert(coinId, price, username);
    }

    @DeleteMapping("/coin/price_alerts/remove")
    @ResponseStatus(HttpStatus.OK)
    public void removePriceAlert(@RequestParam("coin_id") String coinId, @RequestParam("username") String username) {
        coinService.removePriceAlert(coinId, username);
    }

    @GetMapping("/coin/user_coin_info")
    @ResponseStatus(HttpStatus.OK)
    public UserCoinInfoDto getUserCoinInfo(@RequestParam("coin_id") String coinId, @RequestParam("username") String username) {
        return coinService.getUserCoinInfo(coinId, username);
    }

    private List<OhlcDto> getResponseToOhlcObject(String url) {
        RestTemplate restTemplate = new RestTemplate();
        return Arrays.asList(
                restTemplate.getForObject(url, OhlcDto[].class)
        );
    }

    private List<GeskoCoinMarketDto> getResponseToGescoCoinObject(String url) {
        RestTemplate restTemplate = new RestTemplate();
        return Arrays.asList(
                restTemplate.getForObject(url, GeskoCoinMarketDto[].class)
        );
    }
}
