package com.cryptocurrency.entity.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
public class FavoriteCoin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany(cascade = { CascadeType.ALL })
    @JoinTable(
            name = "favorite_coin_list",
            joinColumns = { @JoinColumn(name = "favorite_id") },
            inverseJoinColumns = { @JoinColumn(name = "coin_id") }
    )
    private List<Coin> coinList;

    @OneToOne
    @JoinColumn(name = "username", referencedColumnName = "username")
    private User user;
}
