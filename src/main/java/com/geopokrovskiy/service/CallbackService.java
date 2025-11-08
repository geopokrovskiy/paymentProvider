package com.geopokrovskiy.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.geopokrovskiy.entity.CallbackEntity;
import com.geopokrovskiy.entity.TransactionEntity;
import com.geopokrovskiy.entity.TransactionStatus;
import com.geopokrovskiy.external_dto.callback_service.CallbackResponseDto;
import com.geopokrovskiy.external_dto.callback_service.CreateCallbackDto;
import com.geopokrovskiy.repository.CallbackRepository;
import com.geopokrovskiy.security.HMACEncoder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@Slf4j
@Data
@EnableScheduling
public class CallbackService {
    private final CallbackRepository callbackRepository;
    private final TransactionService transactionService;
    private final HMACEncoder hmacEncoder;
    private final ObjectMapper objectMapper;
    private final WebClient webClient = WebClient.create();
    private final int MAX_ITERATION = 4;

    public Flux<CallbackEntity> updateCallback(UUID transactionId) {
        return getCallbackEntityByTransactionId(transactionId).flatMap(callbackEntity -> {
            int iteration = callbackEntity.getIteration();
            if (iteration <= MAX_ITERATION) {
                callbackEntity.setIteration(iteration + 1);
                return callbackRepository.save(callbackEntity);
            } else {
                return transactionService.getTransactionById(transactionId).flatMap(
                        transactionEntity -> {
                            return transactionService.updateTransactionStatus(transactionEntity, TransactionStatus.FINALIZED);
                        }
                ).flatMap((transaction) -> Mono.empty());
            }
        });
    }

    public Flux<CallbackEntity> getCallbackEntityByTransactionId(UUID transactionId) {
        return callbackRepository.getCallbackEntitiesByTransactionId(transactionId)
                .switchIfEmpty(Mono.just(new CallbackEntity()
                        .toBuilder()
                        .transactionId(transactionId)
                        .build()));
    }

    @Scheduled(fixedRate = 10 * 1000) // send callbacks every 10 seconds
    public void sendCallbacks() {
        transactionService.getAllProcessedTransactions().flatMap(transactionEntity -> {
            return updateCallback(transactionEntity.getId()).flatMap(callbackEntity ->
                    sendTransactionToWebhook(transactionEntity, callbackEntity));
        }).subscribe();
    }

    private Mono<CallbackResponseDto> sendTransactionToWebhook(TransactionEntity transactionEntity, CallbackEntity callbackEntity) {

        CreateCallbackDto createCallbackDto;
        if (transactionEntity.getId() != null) {
            createCallbackDto = new CreateCallbackDto();
            createCallbackDto.setProvider("fake_provider");
            createCallbackDto.setType(transactionEntity.getTransactionType().name());
            createCallbackDto.setProviderTransactionUid(transactionEntity.getId().toString());
            createCallbackDto.setTransactionStatus(transactionEntity.getTransactionStatus().name());
        } else {
            return Mono.error(new RuntimeException("TransactionEntity id is null"));
        }

        String xSignatureHeader = calculateXSignature(createCallbackDto);

        log.info("Sending callback {} to {}", createCallbackDto, transactionEntity.getNotificationURL());
        return webClient.post()
                .uri(transactionEntity.getNotificationURL())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(createCallbackDto))
                .header("X-Signature", xSignatureHeader)
                .header("Cookie", "fake-provider")
                .retrieve()
                .bodyToMono(CallbackResponseDto.class)
                .flatMap(resp -> {
                    log.info("Successfully sent callback to {} ", transactionEntity.getNotificationURL());
                    return updateWebhookIteration(callbackEntity, MAX_ITERATION + 1).thenReturn(resp);
                })
                .doOnError(resp -> {
                    log.error("Error when trying to call {}, cause is {}", transactionEntity.getNotificationURL(),
                            resp.getMessage());
                });
    }

    private Mono<CallbackEntity> updateWebhookIteration(CallbackEntity callbackEntity, int iteration) {
        callbackEntity.setIteration(iteration);
        return callbackRepository.save(callbackEntity);
    }

    private String calculateXSignature(CreateCallbackDto createCallbackDto) {
        try {
            String data = objectMapper.writeValueAsString(createCallbackDto);
            return hmacEncoder.calculateHMACSignature(data);
        } catch (Exception e) {
            throw new RuntimeException("Error during X-Signature generation", e);
        }
    }
}


