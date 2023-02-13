package com.cryptocurrency.repository;

import com.cryptocurrency.entity.domain.PriceAlerts;
import com.cryptocurrency.entity.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PriceAlertsRepository extends JpaRepository<PriceAlerts, Long> {

    Optional<PriceAlerts> findByUser(User user);
}