package com.cryptocurrency.service.portfolio;

import com.cryptocurrency.entity.domain.Coin;
import com.cryptocurrency.entity.domain.Portfolio;
import com.cryptocurrency.entity.domain.PortfolioCoin;
import com.cryptocurrency.entity.domain.User;
import com.cryptocurrency.entity.dto.AddCoinPortfolioDto;
import com.cryptocurrency.exception.IncorrectDataException;
import com.cryptocurrency.exception.OperationExecutionException;
import com.cryptocurrency.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PortfolioServiceImpl implements PortfolioService {

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Autowired
    private CoinRepository coinRepository;

    @Autowired
    private CoinMarketRepository coinMarketRepository;

    @Autowired
    private DesignationRepository designationRepository;

    @Autowired
    private PortfolioCoinRepository portfolioCoinRepository;

    @Override
    public List<Portfolio> findAllByUser(User user, String currency) {
        List<Portfolio> portfolios = portfolioRepository.findPortfoliosByUser(user);
        portfolios.forEach(portfolio -> portfolio.getPortfolioCoins()
                .removeIf(portfolioCoin -> !portfolioCoin.getDesignation().getName().equals(currency)));

        portfolios.forEach(portfolio -> portfolio.getPortfolioCoins()
                .forEach(portfolioCoin -> portfolioCoin.getCoin().getCoinMarketList()
                        .removeIf(coinMarket -> !coinMarket.getDesignation().getName().equals(currency))));

        return portfolios;
    }

    @Override
    public Portfolio save(Portfolio portfolio, User user) {
        portfolio.setUser(user);
        return portfolioRepository.save(portfolio);
    }

    @Override
    public Portfolio addPortfolioCoin(User user, AddCoinPortfolioDto addCoinPortfolioDto) {
        Portfolio portfolio = portfolioRepository.findPortfoliosByUser(user).stream()
                .filter(pObj -> pObj.getName().equals(addCoinPortfolioDto.getPortfolioName()))
                .collect(Collectors.toList()).get(0);

        Coin coin = coinRepository.findById(addCoinPortfolioDto.getCoinId())
                .orElseThrow(() -> new IncorrectDataException("Coin not found"));

        if (portfolio.getPortfolioCoins().stream().noneMatch(portfolioCoin -> portfolioCoin.getCoin().equals(coin))) {

            designationRepository.findAll().forEach(designation -> portfolio.getPortfolioCoins().add(
                    PortfolioCoin.builder()
                            .coin(coin)
                            .designation(designation)
                            .portfolio(portfolio)
                            .buyPrice(
                                    coinMarketRepository.findByCoinIdAndDesignation_Name(
                                            coin.getId(), designation.getName()).getCurrentPrice()
                            )
                            .quantity(addCoinPortfolioDto.getQuantity()).build()
            ));
        } else {
            designationRepository.findAll().forEach(designation -> {
                PortfolioCoin portfolioCoin = portfolio.getPortfolioCoins().stream()
                        .filter(portfolioCoinObj ->
                                portfolioCoinObj.getCoin().equals(coin) && portfolioCoinObj.getDesignation().equals(designation))
                        .findFirst().get();
                portfolioCoin.setQuantity(portfolioCoin.getQuantity() + addCoinPortfolioDto.getQuantity());
                portfolioCoin.setBuyPrice(
                        (portfolioCoin.getBuyPrice()*portfolioCoin.getQuantity() +
                                addCoinPortfolioDto.getQuantity()*addCoinPortfolioDto.getBuyPrice())
                        / (portfolioCoin.getQuantity() + addCoinPortfolioDto.getQuantity())
                );
                portfolioCoinRepository.save(portfolioCoin);
            });
        }
        return portfolioRepository.save(portfolio);
    }

    @Override
    public Portfolio edit(Portfolio portfolio) {
        Portfolio persistedPortfolio = portfolioRepository.findById(portfolio.getId())
                .orElseThrow(() -> new IncorrectDataException("Portfolio not found"));

        persistedPortfolio.setName(portfolio.getName());
        return portfolioRepository.save(persistedPortfolio);
    }

    @Override
    public void removePortfolioCoin(Long portfolioId, String coinId) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new IncorrectDataException("Portfolio not found"));

        portfolio.getPortfolioCoins().stream()
                .filter(portfolioCoin -> portfolioCoin.getCoin().getId().equals(coinId))
                .forEach(portfolioCoin -> portfolioCoinRepository.removeByCoin(portfolioCoin.getCoin()));

        portfolio.getPortfolioCoins().removeIf(portfolioCoin -> portfolioCoin.getCoin().getId().equals(coinId));
        portfolioRepository.save(portfolio);
    }

    @Override
    public void remove(Long id) {
        if (portfolioRepository.removeById(id) == 1)
            throw new OperationExecutionException("Portfolio not deleted");
    }
}
