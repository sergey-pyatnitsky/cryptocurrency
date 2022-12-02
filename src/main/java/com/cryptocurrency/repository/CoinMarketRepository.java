package com.cryptocurrency.repository;

import com.cryptocurrency.entity.domain.CoinMarket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoinMarketRepository extends JpaRepository<CoinMarket, Long> {
}