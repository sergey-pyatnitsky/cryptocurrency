package com.cryptocurrency.repository;

import com.cryptocurrency.entity.domain.Coin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CoinRepository extends JpaRepository<Coin, String> {

    @Query("select name from Coin")
    List<String> findCoinsName();

    @Query("select id from Coin")
    List<String> findCoinsId();
}