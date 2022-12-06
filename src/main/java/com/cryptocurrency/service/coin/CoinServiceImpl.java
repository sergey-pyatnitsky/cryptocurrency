package com.cryptocurrency.service.coin;

import com.cryptocurrency.entity.domain.CoinMarket;
import com.cryptocurrency.entity.dto.gesko.GeskoCoinMarketDto;
import com.cryptocurrency.mapper.CoinMarketMapper;
import com.cryptocurrency.mapper.gesko.GeskoMapper;
import com.cryptocurrency.repository.CoinMarketRepository;
import com.cryptocurrency.repository.CoinRepository;
import com.cryptocurrency.repository.DesignationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CoinServiceImpl implements CoinService {

    @Autowired
    private CoinRepository coinRepository;

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
    public List<CoinMarket> persistGeskoInfo(List<GeskoCoinMarketDto> geskoCoinMarketDtoList, String currency) {
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

            return coinMarketRepository.save(coinMarket);

        }).collect(Collectors.toList());
    }
}
