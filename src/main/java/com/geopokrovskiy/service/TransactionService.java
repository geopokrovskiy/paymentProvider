package com.geopokrovskiy.service;

import com.geopokrovskiy.entity.CardEntity;
import com.geopokrovskiy.entity.TransactionEntity;
import com.geopokrovskiy.entity.TransactionStatus;
import com.geopokrovskiy.entity.TransactionType;
import com.geopokrovskiy.exception.ApiException;
import com.geopokrovskiy.exception.ErrorCodes;
import com.geopokrovskiy.repository.TransactionRepository;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
    @Autowired
    private final TransactionalOperator transactionalOperator;

    Mono<TransactionEntity> getTransactionById(UUID id) {
        return this.transactionRepository.getTransactionEntityById(id);
    }

    public Mono<TransactionEntity> topUp(TransactionEntity transactionEntity) {
        CardEntity cardEntity = transactionEntity.getCard();
        return this.cardService.verifyCard(cardEntity.getExpirationDate(), cardEntity.getCardNumber()).flatMap(
                b -> {
                    if (b) {
                        return this.customerService.addNewCustomer(transactionEntity.getCustomer()).flatMap(customer -> {
                            return this.cardService.addNewCard(transactionEntity.getCard()
                                    .toBuilder().
                                    customerId(customer.getId())
                                    .build()).flatMap(card -> {
                                return this.saveTopUp(transactionEntity, TransactionStatus.IN_PROGRESS, card);
                            });
                        });
                    } else {
                        return Mono.error(new ApiException("Invalid card", ErrorCodes.INVALID_CARD));
                    }
                }
        );
    }

    public Mono<TransactionEntity> payOut(TransactionEntity transactionEntity) {
        UUID accountId = transactionEntity.getAccountId();
        return this.accountService.getAccountByUUID(accountId).flatMap(account -> {
                    return this.accountService.withdrawMoneyFromAccount(account, transactionEntity.getAmount());
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
                            .cardId(card.getId())
                            .transactionType(TransactionType.WITHDRAWAL)
                            .language(transactionEntity.getLanguage())
                            .build());
                }).switchIfEmpty(Mono.error(new ApiException("Unknown card", ErrorCodes.UNKNOWN_CARD)));
    }

    private Mono<TransactionEntity> saveTopUp(TransactionEntity transactionEntity, TransactionStatus status, CardEntity cardEntity) {
        return transactionRepository.save(transactionEntity.toBuilder()
                .accountId(transactionEntity.getAccountId())
                .amount(transactionEntity.getAmount())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .transactionStatus(status)
                .notificationURL(transactionEntity.getNotificationURL())
                .cardId(cardEntity.getId())
                .transactionType(TransactionType.TOP_UP)
                .language(transactionEntity.getLanguage())
                .build());
    }

    public Flux<TransactionEntity> getAllTopUpTransactionsInProgress() {
        return transactionRepository.findAll().filter(transactionEntity ->
                transactionEntity.getTransactionStatus().equals(TransactionStatus.IN_PROGRESS) &&
                        transactionEntity.getTransactionType().equals(TransactionType.TOP_UP));
    }

    public Flux<TransactionEntity> getAllPayOutTransactionsInProgress() {
        return transactionRepository.findAll().filter(transactionEntity ->
                transactionEntity.getTransactionStatus().equals(TransactionStatus.IN_PROGRESS) &&
                        transactionEntity.getTransactionType().equals(TransactionType.WITHDRAWAL));
    }

    public Flux<TransactionEntity> getAllProcessedTransactions() {
        return transactionRepository.findAll().filter(transactionEntity ->
                !transactionEntity.getTransactionStatus().equals(TransactionStatus.IN_PROGRESS));
    }

    private Mono<TransactionEntity> updateTransactionStatus(TransactionEntity transactionEntity, TransactionStatus status) {
        transactionEntity.setTransactionStatus(status);
        return transactionRepository.save(transactionEntity);
    }

    public Mono<TransactionEntity> completeTransaction(TransactionEntity transactionEntity, TransactionStatus transactionStatus) {
        if (transactionEntity.getTransactionType().equals(TransactionType.TOP_UP) && transactionStatus.equals(TransactionStatus.SUCCESS)) {
            UUID accountId = transactionEntity.getAccountId();
            return accountService.getAccountByUUID(accountId).flatMap(account -> {
                log.info("Top up transaction {} is successful", transactionEntity.getId());
                return accountService.topUpAccount(account, transactionEntity.getAmount());
            }).flatMap(accountEntity -> {
                return this.updateTransactionStatus(transactionEntity, transactionStatus);
            }).as(transactionalOperator::transactional);
        } else if (transactionEntity.getTransactionType().equals(TransactionType.WITHDRAWAL) && transactionStatus.equals(TransactionStatus.FAILED)) {
            UUID accountId = transactionEntity.getAccountId();
            return accountService.getAccountByUUID(accountId).flatMap(account -> {
                log.info("Pay out transaction {} has been cancelled", transactionEntity.getId());
                return accountService.topUpAccount(account, transactionEntity.getAmount());
            }).flatMap(accountEntity -> {
                return this.updateTransactionStatus(transactionEntity, transactionStatus);
            }).as(transactionalOperator::transactional);
        } else {
            log.info("{} transaction {} has been completed with status {}", transactionEntity.getTransactionType(), transactionEntity.getId(), transactionStatus);
            return this.updateTransactionStatus(transactionEntity, transactionStatus);
        }
    }


}
