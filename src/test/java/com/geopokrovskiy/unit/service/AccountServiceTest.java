package com.geopokrovskiy.unit.service;

import com.geopokrovskiy.entity.AccountEntity;
import com.geopokrovskiy.entity.CurrencyEntity;
import com.geopokrovskiy.entity.MerchantEntity;
import com.geopokrovskiy.repository.AccountRepository;
import com.geopokrovskiy.repository.CurrencyRepository;
import com.geopokrovskiy.repository.MerchantRepository;
import com.geopokrovskiy.service.AccountService;
import com.geopokrovskiy.service.CurrencyService;
import com.geopokrovskiy.service.MerchantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
public class AccountServiceTest {
    private final AccountRepository accountRepository = Mockito.mock(AccountRepository.class);
    private final MerchantRepository merchantRepository = Mockito.mock(MerchantRepository.class);
    private final CurrencyRepository currencyRepository = Mockito.mock(CurrencyRepository.class);
    private final PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
    @InjectMocks
    private AccountService accountService;
    private final String CURRENCY = "TST";
    private final String MERCHANT_USERNAME = "USERNAME";
    private MerchantEntity merchantEntity;
    private CurrencyEntity currencyEntity;

    @BeforeEach
    public void setUp() {
        CurrencyService currencyService = new CurrencyService(currencyRepository);
        MerchantService merchantService = new MerchantService(merchantRepository, passwordEncoder);
        accountService = new AccountService(accountRepository, merchantService, currencyService);
    }

    @Test
    public void testSaveAccount() {
        MerchantEntity merchant = this.getMerchantEntity();
        AccountEntity account = this.getExpectedAccount();
        CurrencyEntity currency = this.getCurrencyEntity();

        Mockito.when(merchantRepository.findByUsername(MERCHANT_USERNAME)).thenReturn(Mono.just(merchant));
        Mockito.when(accountRepository.save(account)).thenReturn(Mono.just(account));
        Mockito.when(currencyRepository.findByCurrencyCode(CURRENCY)).thenReturn(Mono.just(currency));

        Mono<AccountEntity> result = accountService.saveAccount(CURRENCY, merchant.getUsername());

        result.subscribe(
                res -> {
                    verify(merchantRepository, times(1)).findByUsername(MERCHANT_USERNAME);
                    verify(accountRepository, times(1)).save(any());
                    verify(currencyRepository, times(1)).findByCurrencyCode(CURRENCY);
                }
        );
    }

    @Test
    public void testUpdateAccountBalance() {
        AccountEntity account = this.getExpectedAccount();

        Mockito.when(accountRepository.save(any())).thenAnswer(invocation -> {
            AccountEntity updatedAccount = invocation.getArgument(0);
            return Mono.just(updatedAccount);
        });

        Double amount = 500d;

        Mono<AccountEntity> resultMono = accountService.updateAccountBalance(account, amount);

        resultMono.subscribe(
                result -> {
                    verify(accountRepository, times(1)).save(any());
                    verify(merchantRepository, times(0));
                    verify(currencyRepository, times(0));

                    assertEquals(account.getBalance() - amount, result.getBalance());
                }
        );
    }

    private MerchantEntity getMerchantEntity() {
        if (this.merchantEntity == null) {
            this.merchantEntity = new MerchantEntity().toBuilder()
                    .registrationDate(LocalDateTime.now())
                    .country("Country1")
                    .password("pwd")
                    .username(MERCHANT_USERNAME)
                    .id(UUID.randomUUID())
                    .build();
        }
        return this.merchantEntity;
    }

    private AccountEntity getExpectedAccount() {
        return new AccountEntity().toBuilder()
                .balance(1000d)
                .merchantId(this.getMerchantEntity().getId())
                .currencyCode(CURRENCY)
                .id(UUID.randomUUID())
                .build();
    }

    private CurrencyEntity getCurrencyEntity() {
        if (this.currencyEntity == null) {
            this.currencyEntity = new CurrencyEntity().toBuilder()
                    .currencyCode(CURRENCY)
                    .build();
        }
        return this.currencyEntity;
    }
}
