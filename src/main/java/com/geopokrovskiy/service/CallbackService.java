package com.geopokrovskiy.service;

import com.geopokrovskiy.entity.CallbackEntity;
import com.geopokrovskiy.entity.TransactionEntity;
import com.geopokrovskiy.repository.CallbackRepository;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@Slf4j
@Data
@EnableScheduling
public class CallbackService {
    private final CallbackRepository callbackRepository;
    private final TransactionService transactionService;
    private final int MAX_ITERATION = 4;

    public Mono<CallbackEntity> updateCallback(UUID transactionId) {
        return getCallbackEntityByTransactionId(transactionId).flatMap(callbackEntity -> {
            int iteration = callbackEntity.getIteration();
            if (iteration <= MAX_ITERATION) {
                callbackEntity.setIteration(iteration + 1);
                return callbackRepository.save(callbackEntity);
            } else {
                return Mono.empty();
            }
        });
    }

    public Mono<CallbackEntity> getCallbackEntityByTransactionId(UUID transactionId) {
        return callbackRepository.getCallbackEntityByTransactionId(transactionId)
                .switchIfEmpty(Mono.just(new CallbackEntity()
                        .toBuilder()
                        .transactionId(transactionId)
                        .build()));
    }

    @Scheduled(fixedRate = 30 * 1000) // send callbacks every 30 seconds
    public void sendCallbacks() {
        transactionService.getAllProcessedTransactions().flatMap(transactionEntity -> {
            return updateCallback(transactionEntity.getId()).flatMap(callbackEntity ->
                    sendTransactionToWebhook(transactionEntity));
        }).subscribe();
    }

    private Mono<Void> sendTransactionToWebhook(TransactionEntity transactionEntity) {
        WebClient webClient = WebClient.create();

        return webClient.post()
                .uri(transactionEntity.getNotificationURL())
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .body(BodyInserters.fromValue(transactionEntity))
                .retrieve()
                .bodyToMono(Void.class);
    }
}
