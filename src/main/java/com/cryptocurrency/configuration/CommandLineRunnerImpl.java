package com.cryptocurrency.configuration;

import com.cryptocurrency.entity.dto.gesko.GeskoCoinMarketDto;
import com.cryptocurrency.service.coin.CoinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Component
public class CommandLineRunnerImpl implements CommandLineRunner {

    @Autowired
    private CoinService coinService;

    @Override
    public void run(String... args) {
        String[] currencyArray = coinService.getDesignationNames().toArray(new String[0]);

        for (String currency : currencyArray) {
            String url = "https://api.coingecko.com/api/v3/coins/markets?vs_currency="
                    + currency + "&ids=" + String.join(",", coinService.findCoinsIdBySearch(""))
                    + "&order=market_cap_desc&per_page=100&page=1&sparkline=false";
            coinService.persistGeskoInfo(this.getResponseToGescoCoinObject(url), currency);
        }
    }

    private List<GeskoCoinMarketDto> getResponseToGescoCoinObject(String url) {
        RestTemplate restTemplate = new RestTemplate();
        return Arrays.asList(
                restTemplate.getForObject(url, GeskoCoinMarketDto[].class)
        );
    }
}
