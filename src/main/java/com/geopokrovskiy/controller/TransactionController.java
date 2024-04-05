package com.geopokrovskiy.controller;

import com.geopokrovskiy.dto.transaction.TransactionDto;
import com.geopokrovskiy.dto.transaction.TransactionResponseDto;
import com.geopokrovskiy.mapper.transaction.TransactionMapper;
import com.geopokrovskiy.mapper.transaction.TransactionResponseMapper;
import com.geopokrovskiy.security.CredentialsVerifier;
import com.geopokrovskiy.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
public class TransactionController {

    private final CredentialsVerifier credentialsVerifier;
    private final TransactionService transactionService;
    private final TransactionMapper transactionMapper;
    private final TransactionResponseMapper transactionResponseMapper;

    @PostMapping("/top_up")
    public Mono<TransactionResponseDto> topUp(@RequestBody TransactionDto transaction, @RequestHeader("Authorization") String authorizationHeader) {
        return this.credentialsVerifier.verifyCredentials(authorizationHeader).flatMap(username -> {
            return this.transactionService.topUp(transactionMapper.map(transaction)).map(transactionResponseMapper::map);
        });
    }

    @PostMapping("/pay_out")
    public Mono<TransactionResponseDto> payOut(@RequestBody TransactionDto transaction, @RequestHeader("Authorization") String authorizationHeader) {
        return this.credentialsVerifier.verifyCredentials(authorizationHeader).flatMap(username -> {
            return this.transactionService.payOut(transactionMapper.map(transaction)).map(transactionResponseMapper::map);
        });
    }
}
