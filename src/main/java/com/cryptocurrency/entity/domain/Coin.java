package com.cryptocurrency.entity.domain;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Coin")
public class Coin {

    @Id
    private String id;
    @Column(nullable = false, unique = true)
    private String name;
    @Column(nullable = false, unique = true, length = 10)
    private String symbol;
    private String image;

//    @Lob
    @Column(name = "ru_description")
    private String ruDescription;

//    @Lob
    @Column(name="en_description")
    private String enDescription;

    @OneToMany(mappedBy = "coin")
    private List<CoinMarket> coinMarketList = new ArrayList<>();
}
