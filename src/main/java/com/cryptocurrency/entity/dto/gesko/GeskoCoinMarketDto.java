package com.cryptocurrency.entity.dto.gesko;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GeskoCoinMarketDto {
    @JsonProperty("id")
    private String id;
    @JsonProperty("current_price")
    private double currentPrice;
    @JsonProperty("market_cap")
    private double marketCap;
    @JsonProperty("fully_diluted_valuation")
    private double fullyDilutedValuation;
    @JsonProperty("high_24h")
    private double high24h;
    @JsonProperty("low_24h")
    private double low24h;
    @JsonProperty("price_change_percentage_24h")
    private double priceChangePercentage24h;
    @JsonProperty("market_cap_change_percentage_24h")
    private double marketCapChangePercentage24h;
    @JsonProperty("circulating_supply")
    private double circulatingSupply;
    @JsonProperty("total_supply")
    private double totalSupply;
    @JsonProperty("last_updated")
    private String lastUpdated;
}
