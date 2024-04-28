package com.geopokrovskiy.unit.service;

import com.geopokrovskiy.entity.CardEntity;
import com.geopokrovskiy.repository.CardRepository;
import com.geopokrovskiy.service.CardService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.mockito.Mockito.*;


@SpringBootTest
public class CardServiceTest {
    private final CardRepository cardRepository = Mockito.mock(CardRepository.class);
    private final String CORRECT_CARD_NUMBER = "1234567812345678";
    private final String INCORRECT_CARD_NUMBER = "1234567812345";
    @InjectMocks
    private CardService cardService;

    @Test
    public void testGetCardByNumber() {
        CardEntity expectedCard = this.getExpectedCard();
        Mockito.when(cardRepository.findByCardNumber(CORRECT_CARD_NUMBER)).thenReturn(Mono.just(expectedCard));

        Mono<CardEntity> result = this.cardService.getCardByNumber(CORRECT_CARD_NUMBER);

        result.subscribe(
                res -> {
                    verify(cardRepository, times(1)).findByCardNumber(any());
                    verify(res.getCardNumber().equals(CORRECT_CARD_NUMBER));
                }
        );
    }

    @Test
    public void testAddNewCard() {
        CardEntity newCard = this.getExpectedCardWithoutId();
        CardEntity savedCard = this.getExpectedCard();

        Mockito.when(cardRepository.save(newCard)).thenReturn(Mono.just(savedCard));
        Mockito.when(cardRepository.findByCardNumber(CORRECT_CARD_NUMBER)).thenReturn(Mono.just(savedCard));

        Mono<CardEntity> result = this.cardService.addNewCard(newCard);

        result.subscribe(
                res -> {
                    verify(cardRepository, times(1)).findByCardNumber(CORRECT_CARD_NUMBER);
                    verify(cardRepository, times(0)).save(any());
                }
        );

    }

    @Test
    public void testVerifyCard() {
        CardEntity validCard = this.getExpectedCard();
        CardEntity invalidCard1 = this.getInvalidCard1();
        CardEntity invalidCard2 = this.getInvalidCard2();

        Mono<Boolean> v1 = cardService.verifyCard(validCard.getExpirationDate(), validCard.getCardNumber());
        Mono<Boolean> v2 = cardService.verifyCard(invalidCard1.getExpirationDate(), invalidCard1.getCardNumber());
        Mono<Boolean> v3 = cardService.verifyCard(invalidCard2.getExpirationDate(), invalidCard2.getCardNumber());

        v1.subscribe(
                res -> {
                    verify(res);
                }
        );

        v2.subscribe(
                res -> {
                    verify(!res);
                }
        );

        v3.subscribe(
                res -> {
                    verify(!res);
                }
        );


    }

    private CardEntity getExpectedCard() {
        return new CardEntity().toBuilder()
                .id(UUID.randomUUID())
                .cardNumber(CORRECT_CARD_NUMBER)
                .cvv("999")
                .expirationDate("12/99")
                .build();
    }

    private CardEntity getExpectedCardWithoutId() {
        return new CardEntity().toBuilder()
                .cardNumber(CORRECT_CARD_NUMBER)
                .cvv("999")
                .expirationDate("12/99")
                .build();
    }

    private CardEntity getInvalidCard1() {
        return new CardEntity().toBuilder()
                .cardNumber(INCORRECT_CARD_NUMBER)
                .cvv("999")
                .expirationDate("12/99")
                .build();
    }

    private CardEntity getInvalidCard2() {
        return new CardEntity().toBuilder()
                .cardNumber(CORRECT_CARD_NUMBER)
                .cvv("999")
                .expirationDate("03/24")
                .build();
    }
}
