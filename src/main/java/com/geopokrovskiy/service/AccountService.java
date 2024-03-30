package com.geopokrovskiy.service;

import com.geopokrovskiy.entity.AccountEntity;
import com.geopokrovskiy.repository.AccountRepository;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@Data
public class AccountService {

    private final AccountRepository accountRepository;
    private final MerchantService merchantService;
    private final CurrencyService currencyService;

    public Mono<AccountEntity> saveAccount(String currencyCode, String merchantUsername) {
        return this.currencyService.getCurrencyByCode(currencyCode).flatMap(c -> {
                    return this.merchantService.getMerchantByUsername(merchantUsername).flatMap(m -> {
                        AccountEntity accountToSave = new AccountEntity();
                        accountToSave.setMerchantId(m.getUuid());
                        accountToSave.setCurrencyCode(currencyCode);
                        accountToSave.setBalance(0L);
                        return this.accountRepository.save(accountToSave);
                    });
                }).doOnSuccess(a -> log.info("A new account of {} has been created!", merchantUsername))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Currency " + currencyCode + " is unknown")));
    }
}
