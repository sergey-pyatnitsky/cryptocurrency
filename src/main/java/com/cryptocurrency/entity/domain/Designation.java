package com.cryptocurrency.entity.domain;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
public class Designation {

    @Id
    @Column(name = "name", nullable = false, unique = true, length = 10)
    private String name;

    @Column(nullable = false, unique = true, length = 5)
    private String symbol;
}
