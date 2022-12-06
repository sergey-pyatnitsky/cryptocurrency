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

    @OneToOne
    @JoinColumn(name = "designation", referencedColumnName = "name")
    private Designation designation;

    @Column(name = "currentPrice")
    private double currentPrice;

    @Column(name = "market_cap")
    private double marketCap;

    @Column(name = "fully_diluted_valuation")
    private double fullyDilutedValuation;

    @Column(name = "high_24h")
    private double high24h;

    @Column(name = "low_24h")
    private double low24h;

    @Column(name = "price_change_percentage_24h")
    private double priceChangePercentage24h;

    @Column(name = "market_cap_change_percentage_24h")
    private double marketCapChangePercentage24h;

    @Column(name = "circulating_supply")
    private double circulatingSupply;

    @Column(name = "total_supply")
    private double totalSupply;

    @Column(name = "last_updated")
    private String lastUpdated;
}
