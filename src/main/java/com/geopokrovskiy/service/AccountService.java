package com.geopokrovskiy.service;

import com.geopokrovskiy.entity.AccountEntity;
import com.geopokrovskiy.exception.ApiException;
import com.geopokrovskiy.exception.ErrorCodes;
import com.geopokrovskiy.repository.AccountRepository;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

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
                        accountToSave.setMerchantId(m.getId());
                        accountToSave.setCurrencyCode(currencyCode);
                        accountToSave.setBalance(0.0);
                        return this.accountRepository.save(accountToSave);
                    });
                }).doOnSuccess(a -> log.info("A new account of {} has been created!", merchantUsername))
                .switchIfEmpty(Mono.error(new ApiException("Unknown currency " + currencyCode, ErrorCodes.UNKNOWN_CURRENCY)));
    }

    public Flux<AccountEntity> getAccountList(String merchantUsername) {
        return this.merchantService.getMerchantByUsername(merchantUsername).flatMapMany(m -> {
            return this.accountRepository.findByMerchantId(m.getId());
        });
    }

    public Mono<AccountEntity> getAccountByUUID(UUID id) {
        return this.accountRepository.findById(id);
    }

    public Mono<AccountEntity> updateAccountBalance(AccountEntity account, Double amount) {
        return this.accountRepository.save(account.toBuilder()
                .balance(account.getBalance() - amount)
                .build());
    }
}
