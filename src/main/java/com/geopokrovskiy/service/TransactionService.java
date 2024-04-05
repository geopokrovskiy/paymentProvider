package com.geopokrovskiy.service;

import com.geopokrovskiy.entity.CardEntity;
import com.geopokrovskiy.entity.TransactionEntity;
import com.geopokrovskiy.entity.TransactionStatus;
import com.geopokrovskiy.entity.TransactionType;
import com.geopokrovskiy.repository.TransactionRepository;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@Data
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountService accountService;
    private final MerchantService merchantService;
    private final CustomerService customerService;
    private final CardService cardService;

    public Mono<TransactionEntity> topUp(TransactionEntity transactionEntity) {
        LocalDate expirationDate = cardService.getCardExpirationDate(transactionEntity.getCard().getExpirationDate());
        return this.customerService.addNewCustomer(transactionEntity.getCustomer()).flatMap(customer -> {

            return this.cardService.addNewCard(transactionEntity.getCard()
                    .toBuilder()
                    .customerId(customer.getUuid())
                    .build()).flatMap(card -> {
                return expirationDate.isBefore(LocalDate.now())
                        ? this.saveTopUp(transactionEntity, TransactionStatus.FAILED, card)
                        : this.saveTopUp(transactionEntity, TransactionStatus.IN_PROGRESS, card);
            });
        });
    }

    public Mono<TransactionEntity> payOut(TransactionEntity transactionEntity) {
        UUID accountId = transactionEntity.getAccountId();
        return this.accountService.getAccountByUUID(accountId).flatMap(account -> {
                    return this.accountService.updateAccountBalance(account, transactionEntity.getAmount());
                }).flatMap(account -> {
                    return this.cardService.getCardByNumber(transactionEntity.getCard().getCardNumber());
                }).
                flatMap(card -> {
                    return transactionRepository.save(transactionEntity.toBuilder()
                            .accountId(accountId)
                            .amount(transactionEntity.getAmount())
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .transactionStatus(TransactionStatus.IN_PROGRESS)
                            .notificationURL(transactionEntity.getNotificationURL())
                            .cardId(card.getUuid())
                            .transactionType(TransactionType.WITHDRAWAL)
                            .language(transactionEntity.getLanguage())
                            .build());
                });
    }

    private Mono<TransactionEntity> saveTopUp(TransactionEntity transactionEntity, TransactionStatus status, CardEntity cardEntity) {
        return transactionRepository.save(transactionEntity.toBuilder()
                .accountId(transactionEntity.getAccountId())
                .amount(transactionEntity.getAmount())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .transactionStatus(status)
                .notificationURL(transactionEntity.getNotificationURL())
                .cardId(cardEntity.getUuid())
                .transactionType(TransactionType.TOP_UP)
                .language(transactionEntity.getLanguage())
                .build());
    }
}
