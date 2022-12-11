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
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "username", referencedColumnName = "username")
    private User user;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "phone", length = 60)
    private String phone;

    @Column(name = "country")
    private String country;

    @Column(name = "address")
    private String address;

    @Column(name = "image_id", length = 45)
    private String imageId;

    @ManyToMany
    @JoinTable(
            name = "favorite_coin_list",
            joinColumns = { @JoinColumn(name = "coin_id") },
            inverseJoinColumns = { @JoinColumn(name = "favorite_id") }
    )
    private List<FavoriteCoin> favoriteCoinList;
}
