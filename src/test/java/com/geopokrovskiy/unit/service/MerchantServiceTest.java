package com.geopokrovskiy.unit.service;

import com.geopokrovskiy.entity.MerchantEntity;
import com.geopokrovskiy.repository.MerchantRepository;
import com.geopokrovskiy.service.MerchantService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
public class MerchantServiceTest {
    private final MerchantRepository merchantRepository = Mockito.mock(MerchantRepository.class);
    private final PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
    @InjectMocks
    private MerchantService merchantService;

    @Test
    public void testSaveMerchant() {
        MerchantEntity expectedMerchant = this.getExpectedMerchant();
        MerchantEntity newMerchant = this.getNewMerchant();

        Mockito.when(this.merchantRepository.save(any(MerchantEntity.class))).thenReturn(Mono.just(expectedMerchant));
        Mockito.when(this.passwordEncoder.encode("pwd")).thenReturn("encodedPassword");

        Mono<MerchantEntity> result = this.merchantService.saveMerchant(newMerchant);

        result.subscribe(
                saved -> {
                    verify(merchantRepository, times(1)).save(any());
                    verify(passwordEncoder, times(1)).encode("password");
                    verify(passwordEncoder.encode("pwd").equals(saved.getPassword()));
                }
        );
    }

    @Test
    public void testGetMerchantByUsername() {
        MerchantEntity expectedMerchant = this.getExpectedMerchant();

        Mockito.when(this.merchantRepository.findByUsername("user1")).thenReturn(Mono.just(expectedMerchant));

        Mono<MerchantEntity> result = this.merchantService.getMerchantByUsername("user1");

        result.subscribe(
                res -> {
                    verify(merchantRepository, times(1)).findByUsername(any());
                    verify(("user1").equals(res.getUsername()));
                }
        );
    }

    private MerchantEntity getNewMerchant() {
        return new MerchantEntity().toBuilder()
                .username("user1")
                .password("pwd")
                .country("Country1")
                .registrationDate(LocalDateTime.now())
                .build();
    }

    private MerchantEntity getExpectedMerchant() {
        return new MerchantEntity().toBuilder()
                .username("user1")
                .password("encodedPassword")
                .country("Country1")
                .registrationDate(LocalDateTime.now())
                .build();
    }
}
