package com.cryptocurrency.entity.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
public class CoinMarket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "coin_id", nullable = false)
    private Coin coin;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "currentPrice", referencedColumnName = "id")
    private CurrencyValue currentPrice;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "market_cap", referencedColumnName = "id")
    private CurrencyValue marketCap;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "fully_diluted_valuation", referencedColumnName = "id")
    private CurrencyValue fullyDilutedValuation;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "high_24h", referencedColumnName = "id")
    private CurrencyValue high24h;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "low_24h", referencedColumnName = "id")
    private CurrencyValue low24h;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "price_change_percentage_24h", referencedColumnName = "id")
    private CurrencyValue priceChangePercentage24h;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "market_cap_change_percentage_24h", referencedColumnName = "id")
    private CurrencyValue marketCapChangePercentage24h;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "circulating_supply", referencedColumnName = "id")
    private CurrencyValue circulatingSupply;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "total_supply", referencedColumnName = "id")
    private CurrencyValue totalSupply;

    @Column(name = "last_updated")
    private String lastUpdated;
}
