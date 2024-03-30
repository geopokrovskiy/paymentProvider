package com.geopokrovskiy.service;

import com.geopokrovskiy.entity.CurrencyEntity;
import com.geopokrovskiy.repository.CurrencyRepository;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Data
@Slf4j
@Service
public class CurrencyService {
    private final CurrencyRepository currencyRepository;

    public Mono<CurrencyEntity> getCurrencyByCode(String currencyCode) {
        return this.currencyRepository.findByCurrencyCode(currencyCode);
    }
}
