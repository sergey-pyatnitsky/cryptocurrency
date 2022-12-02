package com.cryptocurrency.repository;

import com.cryptocurrency.entity.domain.Portfolio;
import com.cryptocurrency.entity.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {

    int removeById(Long id);

    List<Portfolio> findPortfoliosByUser(User user);
}