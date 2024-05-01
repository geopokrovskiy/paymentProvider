package com.geopokrovskiy.it;

import com.geopokrovskiy.configuration.TestDatabaseConfiguration;
import com.geopokrovskiy.dto.merchant.MerchantDto;
import com.geopokrovskiy.dto.merchant.MerchantResponseDto;
import com.geopokrovskiy.it.utils.ControllerITUtils;
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
public class MerchantControllerIT {
    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private MerchantRepository merchantRepository;

    @BeforeEach
    public void setUp() {
        merchantRepository.deleteAll().block();
    }

    @Test
    public void testAddMerchant() throws Exception {
        // Given
        MerchantDto merchantDto = ControllerITUtils.getMerchant1();

        // When
        webTestClient.post()
                .uri("/api/v1/merchants/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(merchantDto)
                .exchange()

                // Then
                .expectStatus().isOk()
                .expectBody(MerchantResponseDto.class)
                .value(merchant -> {
                    // Assert the response
                    assertNotNull(merchant);
                    UUID merchantUUID = merchant.getId();
                    assertNotNull(merchantUUID);
                    assertNotNull(merchant.getRegistrationDate());
                    assertEquals(merchantDto.getUsername(), merchant.getUsername());
                    assertEquals(merchantDto.getCountry(), merchant.getCountry());
                });
    }
}

