package com.geopokrovskiy.controller;

import com.geopokrovskiy.dto.AccountDto;
import com.geopokrovskiy.mapper.AccountMapper;
import com.geopokrovskiy.security.CredentialsVerifier;
import com.geopokrovskiy.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final AccountMapper accountMapper;
    private final AccountService accountService;

    private final CredentialsVerifier credentialsVerifier;

    @PostMapping("/add")
    public Mono<AccountDto> addAccount(@RequestParam String currencyCode,
                                       @RequestHeader("Authorization") String authorizationHeader) {
        return this.credentialsVerifier.verifyCredentials(authorizationHeader).flatMap(username -> {
            return this.accountService.saveAccount(currencyCode, username).map(accountMapper::map);
        });
    }

    @GetMapping("/get_account_list")
    public Flux<AccountDto> getAccountList(@RequestHeader("Authorization") String authorizationHeader) {
        return this.credentialsVerifier.verifyCredentials(authorizationHeader).flatMapMany(username -> {
            return this.accountService.getAccountList(username).map(accountMapper::map);
        });
    }
}
