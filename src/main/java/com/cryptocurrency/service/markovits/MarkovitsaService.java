package com.cryptocurrency.service.markovits;

import com.cryptocurrency.entity.dto.gesko.OhlcDto;
import com.cryptocurrency.entity.dto.markovits.MarkovitsDto;
import com.cryptocurrency.entity.dto.markovits.PartsOfStock;

import java.util.List;
import java.util.Map;

public interface MarkovitsaService {
    MarkovitsDto getMarkovitsAnalysis(Map<String, List<OhlcDto>> coinPriceMap, List<PartsOfStock> partsOfStockList);
}
