package com.cryptocurrency.repository;

import com.cryptocurrency.entity.domain.PortfolioCoin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PortfolioCoinRepository extends JpaRepository<PortfolioCoin, Long> {
}