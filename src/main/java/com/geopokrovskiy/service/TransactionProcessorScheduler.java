package com.geopokrovskiy.service;

import com.geopokrovskiy.entity.TransactionEntity;
import com.geopokrovskiy.entity.TransactionStatus;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Random;

@Service
@Data
@Slf4j
@EnableScheduling
public class TransactionProcessorScheduler {
    private final TransactionService transactionService;
    @Scheduled(fixedRate = 10 * 1000) // processes all top up transactions every 10 seconds
    public void processTopUps(){
        Random random = new Random();
        transactionService.getAllTopUpTransactionsInProgress().flatMap(transactionEntity -> {
            int randomNumber = random.nextInt(0, 10);
            TransactionStatus status = randomNumber == 9 ? TransactionStatus.FAILED : TransactionStatus.SUCCESS;
            return transactionService.completeTransaction(transactionEntity, status);
        }).subscribe();
    }

    @Scheduled(fixedRate = 10 * 1000) // processes all pay out transactions every 10 seconds
    public void processPayOuts(){
        Random random = new Random();
        transactionService.getAllPayOutTransactionsInProgress().flatMap(transactionEntity -> {
            int randomNumber = random.nextInt(0, 10);
            TransactionStatus status = randomNumber == 9 ? TransactionStatus.FAILED : TransactionStatus.SUCCESS;
            return transactionService.completeTransaction(transactionEntity, status);
        }).subscribe();
    }
}
