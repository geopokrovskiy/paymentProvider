package com.geopokrovskiy.unit.service;

import com.geopokrovskiy.entity.*;
import com.geopokrovskiy.exception.ApiException;
import com.geopokrovskiy.repository.*;
import com.geopokrovskiy.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
public class TransactionServiceTest {
    private final TransactionRepository transactionRepository = Mockito.mock(TransactionRepository.class);
    private final CustomerRepository customerRepository = Mockito.mock(CustomerRepository.class);
    private final CardRepository cardRepository = Mockito.mock(CardRepository.class);
    private final AccountRepository accountRepository = Mockito.mock(AccountRepository.class);
    private final MerchantRepository merchantRepository = Mockito.mock(MerchantRepository.class);
    private final CurrencyRepository currencyRepository = Mockito.mock(CurrencyRepository.class);
    private final PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
    private final TransactionalOperator transactionalOperator = Mockito.mock(TransactionalOperator.class);
    private MerchantEntity merchantEntity;
    private AccountEntity accountEntity;
    private CustomerEntity customerEntity;
    private final String MERCHANT_USERNAME = "MERCHANT_USERNAME";
    private final String CUSTOMER_USERNAME = "CUSTOMER_USERNAME";
    private final String CURRENCY = "TST";
    private final String CORRECT_CARD_NUMBER = "1234567887654321";
    private final String INCORRECT_CARD_NUMBER = "12345677654321";
    private final String NOTIFICATION_URL = "http://localhost:8080/proselyte.webhook";
    @InjectMocks
    private TransactionService transactionService;

    @BeforeEach
    public void setUp() {
        CustomerService customerService = new CustomerService(customerRepository);
        CardService cardService = new CardService(cardRepository);
        CurrencyService currencyService = new CurrencyService(currencyRepository);
        MerchantService merchantService = new MerchantService(merchantRepository, passwordEncoder);
        AccountService accountService = new AccountService(accountRepository, merchantService, currencyService);
        transactionService = new TransactionService(transactionRepository, accountService, merchantService, customerService, cardService, transactionalOperator);
    }

    @Test
    public void testTopUpWithValidCard() {
        TransactionEntity expectedTransaction = this.getValidTransactionEntity();
        CardEntity cardEntity = expectedTransaction.getCard();
        CustomerEntity customerEntity = expectedTransaction.getCustomer();


        Mockito.when(cardRepository.save(cardEntity)).thenReturn(Mono.just(cardEntity));
        Mockito.when(customerRepository.save(customerEntity)).thenReturn(Mono.just(customerEntity));

        Mockito.when(this.transactionRepository.save(expectedTransaction)).thenAnswer(invocation -> {
            TransactionEntity updatedTransaction = invocation.getArgument(0);
            return Mono.just(updatedTransaction);
        });

        Mono<TransactionEntity> result = transactionService.topUp(expectedTransaction);


        result.subscribe(
                res -> {
                    verify(transactionRepository, times(1)).save(expectedTransaction);
                    verify(cardRepository, times(1)).save(cardEntity);
                    verify(accountRepository, times(0)).save(any());
                    verify(currencyRepository, times(0)).save(any());
                    verify(merchantRepository, times(0)).save(any());

                    assertEquals(TransactionType.TOP_UP, res.getTransactionType());
                    assertEquals(TransactionStatus.IN_PROGRESS, res.getTransactionStatus());
                }
        );
    }

    @Test
    public void testTopUpWithInvalidCard() {
        TransactionEntity expectedTransaction = this.getInvalidTransactionEntity();
        CardEntity cardEntity = expectedTransaction.getCard();
        CustomerEntity customerEntity = expectedTransaction.getCustomer();

        Mockito.when(this.transactionRepository.save(expectedTransaction)).thenReturn(Mono.just(expectedTransaction));
        Mockito.when(cardRepository.save(cardEntity)).thenReturn(Mono.just(cardEntity));
        Mockito.when(customerRepository.save(customerEntity)).thenReturn(Mono.just(customerEntity));

        assertThrows(ApiException.class, () -> {
            Mono<TransactionEntity> result = transactionService.topUp(expectedTransaction);
            result.block();
        });

        verify(transactionRepository, times(0)).save(any());
        verify(cardRepository, times(0)).save(any());
        verify(accountRepository, times(0)).save(any());
        verify(currencyRepository, times(0)).save(any());
        verify(merchantRepository, times(0)).save(any());

    }

    @Test
    public void testPayOut() {
        AccountEntity accountEntity = this.getAccountEntity();
        UUID accountEntityUUID = accountEntity.getId();
        TransactionEntity expectedTransaction = this.getValidTransactionEntity();
        CardEntity cardEntity = expectedTransaction.getCard();
        String cardNumber = cardEntity.getCardNumber();

        Mockito.when(accountRepository.findById(accountEntityUUID)).thenReturn(Mono.just(accountEntity));
        Mockito.when(cardRepository.findByCardNumber(cardNumber)).thenReturn(Mono.just(cardEntity));

        Mockito.when(transactionRepository.save(expectedTransaction)).thenAnswer(invocation -> {
            TransactionEntity updatedTransaction = invocation.getArgument(0);
            return Mono.just(updatedTransaction);
        });

        Mockito.when(accountRepository.save(any())).thenAnswer(invocation -> {
            AccountEntity updatedAccount = invocation.getArgument(0);
            return Mono.just(updatedAccount);
        });

        Mono<TransactionEntity> result = transactionService.payOut(expectedTransaction);

        result.subscribe(res -> {
            verify(transactionRepository, times(1)).save(expectedTransaction);
            verify(cardRepository, times(0)).save(cardEntity);
            verify(cardRepository, times(1)).findByCardNumber(cardNumber);
            verify(accountRepository, times(1)).save(accountEntity);
            verify(accountRepository, times(1)).findById(accountEntityUUID);
            verify(currencyRepository, times(0)).save(any());
            verify(merchantRepository, times(0)).save(any());

            assertEquals(accountEntity.getBalance(), 500);
            assertEquals(expectedTransaction.getTransactionType(), TransactionType.WITHDRAWAL);
            assertEquals(expectedTransaction.getTransactionStatus(), TransactionStatus.IN_PROGRESS);
        });
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

    private AccountEntity getAccountEntity() {
        if (this.accountEntity == null) {
            this.accountEntity = new AccountEntity().toBuilder()
                    .balance(0d)
                    .merchantId(this.getMerchantEntity().getId())
                    .currencyCode(CURRENCY)
                    .id(UUID.randomUUID())
                    .build();
        }
        return this.accountEntity;
    }

    private CustomerEntity getCustomerEntity() {
        if (this.customerEntity == null) {
            this.customerEntity = new CustomerEntity().toBuilder()
                    .country("Country1")
                    .firstName("fn1")
                    .lastName("ln1")
                    .username(CUSTOMER_USERNAME)
                    .id(UUID.randomUUID())
                    .build();
        }
        return this.customerEntity;
    }

    private CardEntity getValidCard() {
        return new CardEntity().toBuilder()
                .id(UUID.randomUUID())
                .cardNumber(CORRECT_CARD_NUMBER)
                .customerId(this.getCustomerEntity().getId())
                .cvv("999")
                .expirationDate("12/99")
                .build();
    }

    private CardEntity getInvalidCard() {
        return new CardEntity().toBuilder()
                .id(UUID.randomUUID())
                .cardNumber(INCORRECT_CARD_NUMBER)
                .customerId(this.getCustomerEntity().getId())
                .cvv("999")
                .expirationDate("12/99")
                .build();
    }

    private TransactionEntity getValidTransactionEntity() {
        return new TransactionEntity().toBuilder()
                .id(UUID.randomUUID())
                .accountId(this.getAccountEntity().getId())
                .notificationURL(NOTIFICATION_URL)
                .language("EN")
                .amount(500d)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .card(this.getValidCard())
                .customer(this.getCustomerEntity())
                .build();
    }

    private TransactionEntity getInvalidTransactionEntity() {
        return new TransactionEntity().toBuilder()
                .id(UUID.randomUUID())
                .accountId(this.getAccountEntity().getId())
                .notificationURL(NOTIFICATION_URL)
                .language("EN")
                .amount(500d)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .card(this.getInvalidCard())
                .customer(this.getCustomerEntity())
                .build();
    }


}
