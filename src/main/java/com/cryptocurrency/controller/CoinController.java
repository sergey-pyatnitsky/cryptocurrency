package com.cryptocurrency.controller;

import com.cryptocurrency.entity.dto.CoinMarketDto;
import com.cryptocurrency.entity.dto.gesko.GeskoCoinMarketDto;
import com.cryptocurrency.mapper.CoinMarketMapper;
import com.cryptocurrency.service.coin.CoinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/coin")
public class CoinController {

    @Autowired
    private CoinService coinService;

    @Autowired
    private CoinMarketMapper coinMarketMapper;

    @GetMapping("/getCoinsList/{currency}")
    public List<CoinMarketDto> getCoinsList(@PathVariable("currency") String currency) {
        String url = "https://api.coingecko.com/api/v3/coins/markets?vs_currency="
                + currency + "&ids=" + String.join(",", coinService.findCoinsId())
                + "&order=market_cap_desc&per_page=100&page=1&sparkline=false";

        return coinMarketMapper.toDtoList(
                coinService.persistGeskoInfo(this.getResponseToGescoCoinObject(url), currency)
        );
    }

    private List<GeskoCoinMarketDto> getResponseToGescoCoinObject(String url) {
        RestTemplate restTemplate = new RestTemplate();
        return Arrays.asList(
                restTemplate.getForObject(url, GeskoCoinMarketDto[].class)
        );
    }
}
