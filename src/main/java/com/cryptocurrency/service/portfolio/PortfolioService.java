package com.cryptocurrency.service.portfolio;

import com.cryptocurrency.entity.domain.Portfolio;
import com.cryptocurrency.entity.domain.User;
import com.cryptocurrency.entity.dto.AddCoinPortfolioDto;

import java.util.List;

public interface PortfolioService {

    List<Portfolio> findAllByUser(User user, String currency);
    Portfolio save(Portfolio portfolio, User user);
    Portfolio addPortfolioCoin(User user, AddCoinPortfolioDto addCoinPortfolioDto);
    Portfolio edit(Portfolio portfolio);
    void removePortfolioCoin(Long portfolioId, String coinId);
    void remove(Long id);
}
