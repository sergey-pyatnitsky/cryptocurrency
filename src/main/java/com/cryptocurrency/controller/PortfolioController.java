package com.cryptocurrency.controller;

import com.cryptocurrency.entity.domain.User;
import com.cryptocurrency.entity.dto.AddCoinPortfolioDto;
import com.cryptocurrency.entity.dto.PortfolioDto;
import com.cryptocurrency.exception.IncorrectDataException;
import com.cryptocurrency.mapper.PortfolioMapper;
import com.cryptocurrency.service.portfolio.PortfolioService;
import com.cryptocurrency.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/portfolio")
@CrossOrigin
public class PortfolioController {

    @Autowired
    private PortfolioService portfolioService;

    @Autowired
    private UserService userService;

    @Autowired
    private PortfolioMapper portfolioMapper;

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody List<PortfolioDto> getAllUserPortfolio(@RequestParam("username") String username,
                                                                @RequestParam("currency") String currency) {
        User user = userService.find(username)
                .orElseThrow(() -> new IncorrectDataException("User not found"));

        return portfolioMapper.toDtoList(portfolioService.findAllByUser(user, currency));
    }

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody PortfolioDto addPortfolioToUser(@RequestParam("username") String username,
                                                         @RequestBody PortfolioDto portfolioDto) {
        User user = userService.find(username)
                .orElseThrow(() -> new IncorrectDataException("User not found"));

        return portfolioMapper.toDto(
                portfolioService.save(portfolioMapper.toModal(portfolioDto), user)
        );
    }

    @PostMapping("/coin/add")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody PortfolioDto addCoinToUserPortfolio(@RequestBody AddCoinPortfolioDto addCoinPortfolioDto) {
        User user = userService.find(addCoinPortfolioDto.getUsername())
                .orElseThrow(() -> new IncorrectDataException("User not found"));

        return portfolioMapper.toDto(
                portfolioService.addPortfolioCoin(user, addCoinPortfolioDto)
        );
    }

    @PostMapping("/edit")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody PortfolioDto editPotfolioToUser(@RequestParam("username") String username,
                                                         @RequestParam PortfolioDto portfolioDto) {
        return portfolioMapper.toDto(
                portfolioService.edit(
                        portfolioMapper.toModal(portfolioDto)
                )
        );
    }

    @DeleteMapping("/coin/remove")
    @ResponseStatus(HttpStatus.OK)
    public void removePotfolioCoin(@RequestParam("portfolio_id") Long portfolio_id,
                                   @RequestParam("coin_id") String coinId) {
        portfolioService.removePortfolioCoin(portfolio_id, coinId);
    }

    @DeleteMapping("/remove")
    @ResponseStatus(HttpStatus.OK)
    public void removePotfolio(@RequestParam("portfolio_id") Long portfolio_id) {
        portfolioService.remove(portfolio_id);
    }

}
