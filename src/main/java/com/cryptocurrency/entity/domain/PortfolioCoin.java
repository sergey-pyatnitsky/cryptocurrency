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
public class PortfolioCoin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "designation_name", nullable = false)
    private Designation designation;

    @OneToOne
    @JoinColumn(name = "portfolio", referencedColumnName = "id")
    private Portfolio portfolio;

    @ManyToOne
    @JoinColumn(name = "coin_id", nullable = false)
    private Coin coin;

    @Column(name = "buy_price")
    private Long buyPrice;
}
