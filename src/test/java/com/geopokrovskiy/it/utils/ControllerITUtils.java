package com.geopokrovskiy.it.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.geopokrovskiy.dto.CardDto;
import com.geopokrovskiy.dto.CustomerDto;
import com.geopokrovskiy.dto.merchant.MerchantDto;
import com.geopokrovskiy.dto.transaction.TransactionDto;
import org.json.JSONObject;

import java.util.UUID;

public class ControllerITUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String NOTIFICATION_URL = "http://localhost:8080/proselyte.webhook";
    private static final String CORRECT_CARD_NUMBER = "1234567887654321";
    private static final String INCORRECT_CARD_NUMBER = "12345677654321";

    public static MerchantDto getMerchant1() throws Exception {
        JSONObject requestBody = new JSONObject();
        requestBody.put("username", "user1");
        requestBody.put("country", "Country1");
        requestBody.put("password", "Password$");

        return objectMapper.readValue(requestBody.toString(), MerchantDto.class);
    }

    public static MerchantDto getMerchant2() throws Exception {
        JSONObject requestBody = new JSONObject();
        requestBody.put("username", "user2");
        requestBody.put("country", "Country1");
        requestBody.put("password", "Password$");

        return objectMapper.readValue(requestBody.toString(), MerchantDto.class);
    }

    private static JSONObject getCustomer() throws Exception {
        JSONObject customer = new JSONObject();
        customer.put("first_name", "Ivan");
        customer.put("last_name", "Ivanov");
        customer.put("user_name", "customer1");
        customer.put("country", "Country1");
        return customer;
    }

    public static CustomerDto getCustomerDto() throws Exception {
        return objectMapper.readValue(getCustomer().toString(), CustomerDto.class);
    }

    private static JSONObject getValidCard() throws Exception {
        JSONObject card = new JSONObject();
        card.put("card_number", CORRECT_CARD_NUMBER);
        card.put("cvv", 777);
        card.put("expiration_date", "12/99");
        return card;
    }

    public static CardDto getValidCardDto() throws Exception {
        return objectMapper.readValue(getValidCard().toString(), CardDto.class);
    }

    public static TransactionDto getValidTransaction(UUID accountId) throws Exception {
        JSONObject card = getValidCard();
        JSONObject customer = getCustomer();

        JSONObject requestBody = new JSONObject();
        requestBody.put("account_id", accountId);
        requestBody.put("amount", 500);
        requestBody.put("card", card);
        requestBody.put("notification_url", NOTIFICATION_URL);
        requestBody.put("customer", customer);

        return objectMapper.readValue(requestBody.toString(), TransactionDto.class);
    }


    private static JSONObject getInvalidCard() throws Exception {
        JSONObject card = new JSONObject();
        card.put("card_number", INCORRECT_CARD_NUMBER);
        card.put("cvv", 777);
        card.put("expiration_date", "12/99");
        return card;
    }

    public static CardDto getInvalidCardDto() throws Exception {
        return objectMapper.readValue(getInvalidCard().toString(), CardDto.class);
    }

    public static TransactionDto getInvalidTransaction(UUID accountId) throws Exception {
        JSONObject card = getInvalidCard();
        JSONObject customer = getCustomer();

        JSONObject requestBody = new JSONObject();
        requestBody.put("account_id", accountId);
        requestBody.put("amount", 500);
        requestBody.put("card", card);
        requestBody.put("notification_url", NOTIFICATION_URL);
        requestBody.put("customer", customer);

        return objectMapper.readValue(requestBody.toString(), TransactionDto.class);
    }

    public static TransactionDto getValidPayOut(UUID accountId) throws Exception {
        JSONObject card = new JSONObject();
        card.put("card_number", CORRECT_CARD_NUMBER);

        JSONObject customer = getCustomer();

        JSONObject requestBody = new JSONObject();
        requestBody.put("account_id", accountId);
        requestBody.put("amount", 500);
        requestBody.put("card", card);
        requestBody.put("notification_url", NOTIFICATION_URL);
        requestBody.put("customer", customer);

        return objectMapper.readValue(requestBody.toString(), TransactionDto.class);
    }
}
