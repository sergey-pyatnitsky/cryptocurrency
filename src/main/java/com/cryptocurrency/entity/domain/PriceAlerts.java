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
public class PriceAlerts {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(
            name = "alert_list",
            joinColumns = {@JoinColumn(name = "price_alert_id")},
            inverseJoinColumns = {@JoinColumn(name = "alert_id")}
    )
    private List<Alert> alerts;

    @OneToOne
    @JoinColumn(name = "username", referencedColumnName = "username")
    private User user;
}
