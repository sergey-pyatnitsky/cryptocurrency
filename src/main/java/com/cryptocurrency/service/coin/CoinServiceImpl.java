package com.cryptocurrency.service.coin;

import com.cryptocurrency.entity.domain.*;
import com.cryptocurrency.entity.dto.gesko.GeskoCoinMarketDto;
import com.cryptocurrency.exception.IncorrectDataException;
import com.cryptocurrency.exception.NoSuchDataException;
import com.cryptocurrency.mapper.CoinMarketMapper;
import com.cryptocurrency.mapper.gesko.GeskoMapper;
import com.cryptocurrency.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CoinServiceImpl implements CoinService {

    @Autowired
    private CoinRepository coinRepository;

    @Autowired
    private FavoriteCoinRepository favoriteCoinRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CoinMarketRepository coinMarketRepository;

    @Autowired
    private DesignationRepository designationRepository;


    @Autowired
    private GeskoMapper geskoMapper;

    @Autowired
    private CoinMarketMapper coinMarketMapper;

    @Override
    public List<String> findCoinsId() {
        return coinRepository.findCoinsId();
    }

    @Override
    public List<String> findCoinsIdBySearch(String search) {
        return coinRepository.findCoinsIdBySearch(search);
    }

    @Override
    public List<Coin> findTrendingCoins(String currency) {
        List<Coin> coins = coinRepository.findAll();
        coins.forEach(coin ->
                coin.getCoinMarketList().removeIf(coinMarket -> !coinMarket.getDesignation().getName().equals(currency)));
        coins = coins.stream().sorted(Comparator.comparingDouble(o -> o.getCoinMarketList().get(0).getMarketCap()))
                .limit(7).collect(Collectors.toList());
        return coins;
    }

    @Override
    public FavoriteCoin addFavoriteCoin(String coinId, String username) {
        User user = userRepository.findById(username)
                .orElseThrow(() -> new IncorrectDataException("User not found"));

        Coin coin = coinRepository.findById(coinId)
                .orElseThrow(() -> new IncorrectDataException("Coin not found"));

        FavoriteCoin favoriteCoin = favoriteCoinRepository.findByUser(user)
                .orElse(FavoriteCoin.builder().user(user).coinList(Collections.singletonList(coin)).build());
        if(!favoriteCoin.getCoinList().contains(coin))
            favoriteCoin.getCoinList().add(coin);

        return favoriteCoinRepository.save(favoriteCoin);
    }

    @Override
    public List<Coin> getFavoriteCoinsList(String username, String currency) {
        User user = userRepository.findById(username)
                .orElseThrow(() -> new IncorrectDataException("User not found"));

        FavoriteCoin favoriteCoin = favoriteCoinRepository.findByUser(user)
                .orElseThrow(()-> new NoSuchDataException("Favorite coin null"));

        return favoriteCoin.getCoinList().stream()
                .filter(coin -> coin.getCoinMarketList()
                        .removeIf(coinMarket -> !coinMarket.getDesignation().getName().equals(currency)))
                .collect(Collectors.toList());
    }

    @Override
    public List<Coin> persistGeskoInfo(List<GeskoCoinMarketDto> geskoCoinMarketDtoList, String currency) {
        return geskoCoinMarketDtoList.stream().map(geskoCoinMarketDto -> {
            CoinMarket coinMarket = coinMarketMapper.toModal(
                    geskoMapper.toBaseDto(geskoCoinMarketDto)
            );

            if(coinMarketRepository.existsByCoinIdAndDesignation_Name(geskoCoinMarketDto.getId(), currency)){
                CoinMarket persistedObject =
                        coinMarketRepository.findByCoinIdAndDesignation_Name(geskoCoinMarketDto.getId(), currency);

                coinMarket.setId(persistedObject.getId());
                coinMarket.setDesignation(designationRepository.findById(currency).get());
                coinMarket.setCoin(persistedObject.getCoin());
            }
            else {
                coinMarket.setDesignation(designationRepository.findById(currency).get());
                coinMarket.setCoin(coinRepository.findById(geskoCoinMarketDto.getId()).get());
            }
            coinMarket = coinMarketRepository.save(coinMarket);

            Coin coin = coinRepository.findById(coinMarket.getCoin().getId()).get();
            coin.getCoinMarketList().removeIf(coinMarketObj -> !coinMarketObj.getDesignation().getName().equals(currency));
            return coin;

        }).collect(Collectors.toList());
    }

    @Override
    public List<String> getDesignationNames() {
        return designationRepository.findAll().stream()
                .map(Designation::getName)
                .collect(Collectors.toList());
    }
}
