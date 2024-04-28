package com.geopokrovskiy.it;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.geopokrovskiy.dto.merchant.MerchantDto;
import com.geopokrovskiy.dto.merchant.MerchantResponseDto;
import com.geopokrovskiy.repository.MerchantRepository;
import org.json.JSONObject;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MerchantControllerIT {

    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private MerchantRepository merchantRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testAddMerchant() throws Exception {
        // Given
        JSONObject requestBody = new JSONObject();
        requestBody.put("username", "username");
        requestBody.put("country", "Country1");
        requestBody.put("password", "Password$");

        MerchantDto merchantDto = objectMapper.readValue(requestBody.toString(), MerchantDto.class);


        // When
        webTestClient.post()
                .uri("/api/v1/merchants/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(merchantDto)
                .exchange()

                // Then
                .expectStatus().isOk()
                .expectBody(MerchantResponseDto.class)
                .value(merchantResponse -> {
                    // Assert the response
                    assertNotNull(merchantResponse);

                    UUID merchantUUID = merchantResponse.getId();
                    assertNotNull(merchantUUID);
                    assertNotNull(merchantResponse.getRegistrationDate());
                    assertEquals(merchantDto.getUsername(), merchantResponse.getUsername());
                    assertEquals(merchantDto.getCountry(), merchantResponse.getCountry());
/*
                    // Check if the entity has been saved in the database
                    merchantRepository.findById(merchantUUID).subscribe(Assertions::assertNotNull);

                    // Delete the entity from the database
                    merchantRepository.deleteById(merchantUUID);

                    // Check if the entity has been removed from the database
                    merchantRepository.findById(merchantUUID).subscribe(Assertions::assertNull);*/
                });
    }
}

