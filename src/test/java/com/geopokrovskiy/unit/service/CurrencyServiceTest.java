package com.geopokrovskiy.unit.service;

import com.geopokrovskiy.entity.CurrencyEntity;
import com.geopokrovskiy.repository.CurrencyRepository;
import com.geopokrovskiy.service.CurrencyService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.*;

@SpringBootTest
public class CurrencyServiceTest {
    private final CurrencyRepository currencyRepository = Mockito.mock(CurrencyRepository.class);
    @InjectMocks
    private CurrencyService currencyService;

    @Test
    public void testGetCurrencyByCode() {
        CurrencyEntity expectedCurrency = this.getExpectedCurrency();
        Mockito.when(currencyRepository.findByCurrencyCode("TST")).thenReturn(Mono.just(expectedCurrency));

        Mono<CurrencyEntity> result = this.currencyService.getCurrencyByCode("TST");

        result.subscribe(
                res -> {
                    verify(currencyRepository, times(1)).findByCurrencyCode(any());
                    verify("TST").equals(res.getCurrencyCode());
                }
        );
    }

    private CurrencyEntity getExpectedCurrency() {
        return new CurrencyEntity().toBuilder()
                .currencyCode("TST")
                .build();
    }
}
