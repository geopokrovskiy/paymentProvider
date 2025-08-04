package com.geopokrovskiy.it;

import com.geopokrovskiy.configuration.TestDatabaseConfiguration;
import com.geopokrovskiy.dto.transaction.TransactionDto;
import com.geopokrovskiy.dto.transaction.TransactionResponseDto;
import com.geopokrovskiy.entity.AccountEntity;
import com.geopokrovskiy.entity.CardEntity;
import com.geopokrovskiy.entity.TransactionStatus;
import com.geopokrovskiy.exception.ErrorCodes;
import com.geopokrovskiy.it.utils.ControllerITUtils;
import com.geopokrovskiy.mapper.CardMapper;
import com.geopokrovskiy.mapper.CustomerMapper;
import com.geopokrovskiy.mapper.merchant.MerchantMapper;
import com.geopokrovskiy.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Import(TestDatabaseConfiguration.class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class TransactionControllerIT {
    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private MerchantRepository merchantRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private MerchantMapper merchantMapper;
    @Autowired
    private CardMapper cardMapper;
    @Autowired
    private CustomerMapper customerMapper;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private CardRepository cardRepository;

    private final String correctAuthorizationHeader = "Basic dXNlcjE6UGFzc3dvcmQk";
    private final String incorrectAuthorizationHeader = "Basic Incorrect Header";

    @BeforeEach
    public void setUp() throws Exception {
        transactionRepository.deleteAll().block();
        merchantRepository.deleteAll().block();
        accountRepository.deleteAll().block();
        cardRepository.deleteAll().block();
        customerRepository.deleteAll().block();

        // Creation of a merchant
        webTestClient.post().uri("/api/v1/merchants/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(merchantMapper.map(ControllerITUtils.getMerchant1()))
                .exchange();

        // Creation of a new account belonging to the merchant
        webTestClient.post()
                .uri("/api/v1/accounts/add?currencyCode=CHF")
                .header("Authorization", correctAuthorizationHeader)
                .exchange();

    }

    @Test
    public void testTopUp_correctAuthorization_validCard() throws Exception {
        // Given
        Flux<AccountEntity> accountEntityFlux = accountRepository.findAll();
        UUID accountId = accountEntityFlux.toStream().findFirst().get().getId();

        TransactionDto transactionDto = ControllerITUtils.getValidTransaction(accountId);

        // When
        webTestClient.post().uri("/api/v1/payments/top_up")
                .header("Authorization", correctAuthorizationHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(transactionDto)
                .exchange()

                // Then
                .expectStatus().isOk()
                .expectBody(TransactionResponseDto.class)
                .value(transaction -> {
                    assertNotNull(transaction);

                    UUID id = transaction.getId();
                    assertNotNull(id);
                    assertEquals("IN_PROGRESS", transaction.getTransactionStatus());
                });
    }

    @Test
    public void testTopUp_correctAuthorization_invalidCard() throws Exception {
        // Given
        Flux<AccountEntity> accountEntityFlux = accountRepository.findAll();
        UUID accountId = accountEntityFlux.toStream().findFirst().get().getId();

        TransactionDto transactionDto = ControllerITUtils.getInvalidTransaction(accountId);

        // When
        webTestClient.post().uri("/api/v1/payments/top_up")
                .header("Authorization", correctAuthorizationHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(transactionDto)
                .exchange()

                // Then
                .expectStatus().isBadRequest()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.errors[0].message").isEqualTo("Invalid card")
                .jsonPath("$.errors[0].code").isEqualTo(ErrorCodes.INVALID_CARD);
    }

    @Test
    public void testTopUp_incorrectAuthorization_validCard() throws Exception {
        // Given
        Flux<AccountEntity> accountEntityFlux = accountRepository.findAll();
        UUID accountId = accountEntityFlux.toStream().findFirst().get().getId();

        TransactionDto transactionDto = ControllerITUtils.getValidTransaction(accountId);

        // When
        webTestClient.post().uri("/api/v1/payments/top_up")
                .header("Authorization", incorrectAuthorizationHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(transactionDto)
                .exchange()

                // Then
                .expectStatus().isUnauthorized()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.errors[0].message").isEqualTo("Invalid credentials!");
    }

    @Test
    public void testPayOut_correctAuthorization_sufficientFunds_existingCard() throws Exception {
        // Given
        Flux<AccountEntity> accountEntityFlux = accountRepository.findAll();
        AccountEntity account = accountEntityFlux.toStream().findFirst().get();
        AccountEntity updatedAccount = new AccountEntity().toBuilder()
                .id(account.getId())
                .currencyCode("CHF")
                .balance(1000d)
                .merchantId(account.getMerchantId())
                .build();

        this.accountRepository.save(updatedAccount).block();
        this.customerRepository.save(customerMapper.map(ControllerITUtils.getCustomerDto())).block();
        UUID customerId = customerRepository.findByUsername("customer1").block().getId();

        CardEntity newCard = cardMapper.map(ControllerITUtils.getValidCardDto());
        newCard.setCustomerId(customerId);
        this.cardRepository.save(newCard).block();

        TransactionDto transactionDto = ControllerITUtils.getValidPayOut(account.getId());

        // When
        webTestClient.post().uri("/api/v1/payments/pay_out")
                .header("Authorization", correctAuthorizationHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(transactionDto)
                .exchange()

                // Then
                .expectStatus().isOk()
                .expectBody(TransactionResponseDto.class)
                .value(transaction -> {
                    assertNotNull(transaction);

                    UUID id = transaction.getId();
                    assertNotNull(id);
                    assertEquals("IN_PROGRESS", transaction.getTransactionStatus());
                });
    }

    @Test
    public void testPayOut_correctAuthorization_insufficientFunds_existingCard() throws Exception {
        // Given
        Flux<AccountEntity> accountEntityFlux = accountRepository.findAll();
        AccountEntity account = accountEntityFlux.toStream().findFirst().get();
        AccountEntity updatedAccount = new AccountEntity().toBuilder()
                .id(account.getId())
                .currencyCode("CHF")
                .balance(200d)
                .merchantId(account.getMerchantId())
                .build();

        this.accountRepository.save(updatedAccount).block();
        this.customerRepository.save(customerMapper.map(ControllerITUtils.getCustomerDto())).block();
        UUID customerId = customerRepository.findByUsername("customer1").block().getId();

        CardEntity newCard = cardMapper.map(ControllerITUtils.getValidCardDto());
        newCard.setCustomerId(customerId);
        this.cardRepository.save(newCard).block();

        TransactionDto transactionDto = ControllerITUtils.getValidPayOut(account.getId());

        // When
        webTestClient.post().uri("/api/v1/payments/pay_out")
                .header("Authorization", correctAuthorizationHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(transactionDto)
                .exchange()

                // Then
                .expectStatus().isBadRequest()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.errors[0].message").isEqualTo("Insufficient funds")
                .jsonPath("$.errors[0].code").isEqualTo(ErrorCodes.INSUFFICIENT_FUNDS);
    }

    @Test
    public void testPayOut_correctAuthorization_sufficientFunds_non_existingCard() throws Exception {
        // Given
        Flux<AccountEntity> accountEntityFlux = accountRepository.findAll();
        AccountEntity account = accountEntityFlux.toStream().findFirst().get();
        AccountEntity updatedAccount = new AccountEntity().toBuilder()
                .id(account.getId())
                .currencyCode("CHF")
                .balance(2000d)
                .merchantId(account.getMerchantId())
                .build();

        this.accountRepository.save(updatedAccount).block();
        this.customerRepository.save(customerMapper.map(ControllerITUtils.getCustomerDto())).block();
        UUID customerId = customerRepository.findByUsername("customer1").block().getId();

        TransactionDto transactionDto = ControllerITUtils.getValidPayOut(account.getId());

        // When
        webTestClient.post().uri("/api/v1/payments/pay_out")
                .header("Authorization", correctAuthorizationHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(transactionDto)
                .exchange()

                // Then
                .expectStatus().isBadRequest()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.errors[0].message").isEqualTo("Unknown card")
                .jsonPath("$.errors[0].code").isEqualTo(ErrorCodes.UNKNOWN_CARD);
    }

    @Test
    public void testPayOut_incorrectAuthorization_sufficientFunds_existingCard() throws Exception {
        // Given
        Flux<AccountEntity> accountEntityFlux = accountRepository.findAll();
        AccountEntity account = accountEntityFlux.toStream().findFirst().get();
        AccountEntity updatedAccount = new AccountEntity().toBuilder()
                .id(account.getId())
                .currencyCode("CHF")
                .balance(2000d)
                .merchantId(account.getMerchantId())
                .build();

        this.accountRepository.save(updatedAccount).block();
        this.customerRepository.save(customerMapper.map(ControllerITUtils.getCustomerDto())).block();
        UUID customerId = customerRepository.findByUsername("customer1").block().getId();

        TransactionDto transactionDto = ControllerITUtils.getValidPayOut(account.getId());

        // When
        webTestClient.post().uri("/api/v1/payments/pay_out")
                .header("Authorization", incorrectAuthorizationHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(transactionDto)
                .exchange()

                // Then
                .expectStatus().isUnauthorized()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.errors[0].message").isEqualTo("Invalid credentials!");
    }
}
