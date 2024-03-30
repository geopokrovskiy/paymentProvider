package com.geopokrovskiy.service;

import com.geopokrovskiy.entity.MerchantEntity;
import com.geopokrovskiy.repository.MerchantRepository;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Data
@Slf4j
public class MerchantService {
    private final MerchantRepository merchantRepository;
    private final PasswordEncoder passwordEncoder;

    public Mono<MerchantEntity> saveMerchant(MerchantEntity merchant) {
        return this.merchantRepository.save(merchant.toBuilder()
                        .username(merchant.getUsername())
                        .password(passwordEncoder.encode(merchant.getPassword()))
                        .country(merchant.getCountry())
                        .registrationDate(LocalDateTime.now()).build())
                .doOnSuccess(m -> log.info("The merchant {} has been registered!", m));
    }

    public Mono<MerchantEntity> getMerchantByUsername(String username) {
        return this.merchantRepository.findByUsername(username);
    }

    public Mono<MerchantEntity> getMerchantById(UUID uuid) {
        return this.merchantRepository.findById(uuid);
    }

}
