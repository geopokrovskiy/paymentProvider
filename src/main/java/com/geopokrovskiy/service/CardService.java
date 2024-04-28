package com.geopokrovskiy.service;

import com.geopokrovskiy.entity.CardEntity;
import com.geopokrovskiy.repository.CardRepository;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Service
@Data
@Slf4j
public class CardService {
    private final String CARD_REGEX = "\\d{16,20}";
    private final CardRepository cardRepository;

    public Mono<CardEntity> addNewCard(CardEntity card) {
        return this.cardRepository.findByCardNumber(card.getCardNumber())
                .switchIfEmpty(this.cardRepository.save(card));
    }

    public Mono<CardEntity> getCardByNumber(String cardNumber) {
        return this.cardRepository.findByCardNumber(cardNumber);
    }

    public Mono<Boolean> verifyCard(String expirationDate, String cardNumber) {
        return Mono.just(this.verifyCardRegex(cardNumber) && this.verifyCardExpirationDate(expirationDate));
    }

    private LocalDate getCardExpirationDate(String expirationDate) {
        String[] monthAndYear = expirationDate.split("/");
        int month = Integer.parseInt(monthAndYear[0]);
        int year = Integer.parseInt(monthAndYear[1]) + 2000;

        return LocalDate.of(year, month, 1).plusMonths(1);
    }

    private boolean verifyCardExpirationDate(String expirationDate) {
        return this.getCardExpirationDate(expirationDate).isAfter(LocalDate.now());
    }

    private boolean verifyCardRegex(String cardNumber) {
        return cardNumber.matches(CARD_REGEX);
    }
}
