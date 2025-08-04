package com.geopokrovskiy.it;

import com.geopokrovskiy.configuration.TestDatabaseConfiguration;
import com.geopokrovskiy.dto.AccountDto;
import com.geopokrovskiy.exception.ErrorCodes;
import com.geopokrovskiy.it.utils.ControllerITUtils;
import com.geopokrovskiy.repository.AccountRepository;
import com.geopokrovskiy.repository.MerchantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Import(TestDatabaseConfiguration.class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class AccountControllerIT {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private MerchantRepository merchantRepository;
    @Autowired
    private WebTestClient webTestClient;

    @BeforeEach
    public void setUp() throws Exception {
        accountRepository.deleteAll().block();
        merchantRepository.deleteAll().block();

        webTestClient.post().uri("/api/v1/merchants/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(ControllerITUtils.getMerchant1())
                .exchange();

        webTestClient.post().uri("/api/v1/merchants/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(ControllerITUtils.getMerchant2())
                .exchange();
    }

    @Test
    public void testAddAccount_correctAuthorizationHeader_existingCurrency() throws Exception {
        // Given
        String authorizationHeader = "Basic dXNlcjE6UGFzc3dvcmQk";

        UUID merchantId = merchantRepository.findByUsername("user1").block().getId();

        // When
        webTestClient.post()
                .uri("/api/v1/accounts/add?currencyCode=CHF")
                .header("Authorization", authorizationHeader)
                .exchange()

                // Then
                .expectStatus().isOk()
                .expectBody(AccountDto.class)
                .value(acc -> {
                    assertNotNull(acc);

                    UUID id = acc.getId();
                    assertNotNull(id);
                    assertEquals("CHF", acc.getCurrencyCode());
                    assertEquals(merchantId, acc.getMerchantId());
                    assertEquals(0, acc.getBalance());
                });
    }

    @Test
    public void testAddAccount_incorrectAuthorizationHeader_existingCurrency() throws Exception {
        // Given
        String authorizationHeader = "Basic Incorrect Header";

        // When
        webTestClient.post()
                .uri("/api/v1/accounts/add?currencyCode=CHF")
                .header("Authorization", authorizationHeader)
                .exchange()

                // Then
                .expectStatus().isUnauthorized()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.errors[0].message").isEqualTo("Invalid credentials!");
    }

    @Test
    public void testAddAccount_correctAuthorizationHeader_not_existingCurrency() throws Exception {
        // Given
        String authorizationHeader = "Basic dXNlcjE6UGFzc3dvcmQk";

        // When
        webTestClient.post()
                .uri("/api/v1/accounts/add?currencyCode=ZZZ")
                .header("Authorization", authorizationHeader)
                .exchange()

                // Then
                .expectStatus().isBadRequest()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.errors[0].code").isEqualTo(ErrorCodes.UNKNOWN_CURRENCY)
                .jsonPath("$.errors[0].message").isEqualTo("Unknown currency ZZZ");
    }

    @Test
    public void testGetAccount_List_correctAuthorizationHeader() throws Exception {
        // Given
        String authorizationHeader = "Basic dXNlcjE6UGFzc3dvcmQk";

        webTestClient.post()
                .uri("/api/v1/accounts/add?currencyCode=CHF")
                .header("Authorization", authorizationHeader)
                .exchange();

        webTestClient.post()
                .uri("/api/v1/accounts/add?currencyCode=RUB")
                .header("Authorization", authorizationHeader)
                .exchange();

        // When
        webTestClient.get()
                .uri("/api/v1/accounts/get_account_list")
                .header("Authorization", authorizationHeader)
                .exchange()

                // Then
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.size()").isEqualTo(2);
    }

    @Test
    public void testGetAccount_List_incorrectAuthorizationHeader() throws Exception {
        // Given
        String authorizationHeader = "Basic Incorrect header";

        webTestClient.post()
                .uri("/api/v1/accounts/add?currencyCode=CHF")
                .header("Authorization", authorizationHeader)
                .exchange();

        webTestClient.post()
                .uri("/api/v1/accounts/add?currencyCode=RUB")
                .header("Authorization", authorizationHeader)
                .exchange();

        // When
        webTestClient.get()
                .uri("/api/v1/accounts/get_account_list")
                .header("Authorization", authorizationHeader)
                .exchange()

                // Then
                .expectStatus().isUnauthorized()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.errors[0].message").isEqualTo("Invalid credentials!");

    }
}
