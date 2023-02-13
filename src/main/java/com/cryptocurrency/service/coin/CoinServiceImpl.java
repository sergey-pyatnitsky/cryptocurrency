package com.cryptocurrency.service.coin;

import com.cryptocurrency.entity.domain.*;
import com.cryptocurrency.entity.dto.UserCoinInfoDto;
import com.cryptocurrency.entity.dto.gesko.GeskoCoinMarketDto;
import com.cryptocurrency.exception.IncorrectDataException;
import com.cryptocurrency.exception.NoSuchDataException;
import com.cryptocurrency.mapper.CoinMarketMapper;
import com.cryptocurrency.mapper.gesko.GeskoMapper;
import com.cryptocurrency.repository.*;
import com.cryptocurrency.service.email.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
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
    private PriceAlertsRepository priceAlertsRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private GeskoMapper geskoMapper;

    @Autowired
    private CoinMarketMapper coinMarketMapper;
    @Autowired
    private ProfileRepository profileRepository;

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
    public Coin findCoin(String id, String currency) {
        Coin coin = coinRepository.findById(id)
                .orElseThrow(() -> new IncorrectDataException("Coin not found"));
        coin.getCoinMarketList().removeIf(coinMarket -> !coinMarket.getDesignation().getName().equals(currency));
        return coin;
    }

    @Override
    public FavoriteCoin addFavoriteCoin(String coinId, String username) {
        User user = userRepository.findById(username)
                .orElseThrow(() -> new IncorrectDataException("User not found"));

        Coin coin = coinRepository.findById(coinId)
                .orElseThrow(() -> new IncorrectDataException("Coin not found"));

        FavoriteCoin favoriteCoin = favoriteCoinRepository.findByUser(user)
                .orElse(FavoriteCoin.builder().user(user).coinList(Collections.singletonList(coin)).build());
        if (!favoriteCoin.getCoinList().contains(coin))
            favoriteCoin.getCoinList().add(coin);

        return favoriteCoinRepository.save(favoriteCoin);
    }

    @Override
    public List<Coin> getFavoriteCoinsList(String username, String currency) {
        User user = userRepository.findById(username)
                .orElseThrow(() -> new IncorrectDataException("User not found"));

        FavoriteCoin favoriteCoin = favoriteCoinRepository.findByUser(user)
                .orElseThrow(() -> new NoSuchDataException("Favorite coin null"));

        return favoriteCoin.getCoinList().stream()
                .filter(coin -> coin.getCoinMarketList()
                        .removeIf(coinMarket -> !coinMarket.getDesignation().getName().equals(currency)))
                .collect(Collectors.toList());
    }

    @Override
    public PriceAlerts findByUsername(String username) {
        User user = userRepository.findById(username)
                .orElseThrow(() -> new IncorrectDataException("User not found"));

        return priceAlertsRepository.findByUser(user)
                .orElseThrow(() -> new NoSuchDataException("PriceAlert not found"));
    }

    @Override
    public PriceAlerts addPriceAlert(String coinId, Double price, String username, String currency) {
        User user = userRepository.findById(username)
                .orElseThrow(() -> new IncorrectDataException("User not found"));

        Coin coin = coinRepository.findById(coinId)
                .orElseThrow(() -> new IncorrectDataException("Coin not found"));

        Designation designation = designationRepository.findById(currency)
                .orElseThrow(() -> new IncorrectDataException("Designation not found"));

        PriceAlerts priceAlerts = priceAlertsRepository.findByUser(user)
                .orElse(PriceAlerts.builder().user(user)
                        .alerts(Collections.singletonList(
                                Alert.builder().coin(coin).price(price).designation(designation).build())
                        ).build());

        Optional<Alert> alert = priceAlerts.getAlerts().stream()
                .filter(item -> item.getCoin().getId().equals(coin.getId()) && item.getDesignation().equals(designation))
                .findFirst();
        if (!alert.isPresent())
            priceAlerts.getAlerts().add(Alert.builder().coin(coin).price(price).designation(designation).build());

        return priceAlertsRepository.save(priceAlerts);
    }

    @Override
    public PriceAlerts editPriceAlert(String coinId, Double price, String username) {
        User user = userRepository.findById(username)
                .orElseThrow(() -> new IncorrectDataException("User not found"));

        Coin coin = coinRepository.findById(coinId)
                .orElseThrow(() -> new IncorrectDataException("Coin not found"));

        PriceAlerts priceAlerts = priceAlertsRepository.findByUser(user)
                .orElseThrow(() -> new IncorrectDataException("PriceAlerts not found"));

        priceAlerts.getAlerts().stream().filter(alert -> alert.getCoin().getId().equals(coin.getId()))
                .forEach(alert -> alert.setPrice(price));

        return priceAlertsRepository.save(priceAlerts);
    }

    @Override
    public boolean removePriceAlert(String coinId, String username) {
        User user = userRepository.findById(username)
                .orElseThrow(() -> new IncorrectDataException("User not found"));

        Coin coin = coinRepository.findById(coinId)
                .orElseThrow(() -> new IncorrectDataException("Coin not found"));

        PriceAlerts priceAlerts = priceAlertsRepository.findByUser(user)
                .orElseThrow(() -> new IncorrectDataException("PriceAlerts not found"));

        return priceAlerts.getAlerts().removeIf(alert -> alert.getCoin().getId().equals(coin.getId()));
    }

    @Scheduled(fixedDelay = 600000, initialDelay = 100000)
    @Async
    @Override
    public void sendPriceAlerts() {
        log.warn("Scheduled: Sending PriceAlerts");
        List<User> users = userRepository.findAll();
        users.forEach(user -> {
            if (priceAlertsRepository.findByUser(user).isPresent()) {
                PriceAlerts priceAlerts = priceAlertsRepository.findByUser(user).get();
                priceAlerts.getAlerts().forEach(alert -> {
                    if (coinRepository.findById(alert.getCoin().getId()).isPresent()) {
                        Coin coin = coinRepository.findById(alert.getCoin().getId()).get();
                        if (coin.getCoinMarketList()
                                .stream().anyMatch(market -> market.getDesignation().equals(alert.getDesignation()))) {
                            CoinMarket coinMarket = coin.getCoinMarketList()
                                    .stream().filter(market -> market.getDesignation().equals(alert.getDesignation()))
                                    .findFirst().get();
                            if (coinMarket.getCurrentPrice() <= alert.getPrice())
                                emailService.sendSimpleEmail(
                                        profileRepository.findProfileByUser(user).getEmail(),
                                        "Уведомление " + coin.getName() + " (Notification " + coin.getName() + ")",
                                        "Цена " + coin.getName() + " достигла необходимого уровня.\n\n" +
                                                "The price of " + coin.getName() + " has reached the required level.");
                        }
                    }
                });
            }
        });
    }

    @Override
    public UserCoinInfoDto getUserCoinInfo(String coinId, String username) {
        User user = userRepository.findById(username)
                .orElseThrow(() -> new IncorrectDataException("User not found"));

        Coin coin = coinRepository.findById(coinId)
                .orElseThrow(() -> new IncorrectDataException("Coin not found"));

        UserCoinInfoDto userCoinInfoDto = UserCoinInfoDto.builder().isFavorite(false).isExistPriceAlert(false).build();

        if (favoriteCoinRepository.findByUser(user).isPresent()
                && favoriteCoinRepository.findByUser(user).get().getCoinList().contains(coin))
            userCoinInfoDto.setFavorite(true);

        if (priceAlertsRepository.findByUser(user).isPresent()
                && priceAlertsRepository.findByUser(user).get().getAlerts().stream()
                .anyMatch(alert -> alert.getCoin().equals(coin)))
            userCoinInfoDto.setExistPriceAlert(true);
        return userCoinInfoDto;
    }

    @Override
    public List<Coin> persistGeskoInfo(List<GeskoCoinMarketDto> geskoCoinMarketDtoList, String currency) {
        return geskoCoinMarketDtoList.stream().map(geskoCoinMarketDto -> {
            CoinMarket coinMarket = coinMarketMapper.toModal(
                    geskoMapper.toBaseDto(geskoCoinMarketDto)
            );

            if (coinMarketRepository.existsByCoinIdAndDesignation_Name(geskoCoinMarketDto.getId(), currency)) {
                CoinMarket persistedObject =
                        coinMarketRepository.findByCoinIdAndDesignation_Name(geskoCoinMarketDto.getId(), currency);

                coinMarket.setId(persistedObject.getId());
                coinMarket.setDesignation(designationRepository.findById(currency).get());
                coinMarket.setCoin(persistedObject.getCoin());
            } else {
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
