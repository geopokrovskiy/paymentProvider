package com.geopokrovskiy.controller;

import com.geopokrovskiy.dto.MerchantDto;
import com.geopokrovskiy.entity.MerchantEntity;
import com.geopokrovskiy.mapper.MerchantMapper;
import com.geopokrovskiy.service.MerchantService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/merchants")
public class MerchantController {

    private final MerchantService merchantService;
    private final MerchantMapper merchantMapper;

    @PostMapping("/register")
    public Mono<MerchantDto> addMerchant(@RequestBody MerchantDto merchantDto) {
        MerchantEntity merchantEntity = merchantMapper.map(merchantDto);
        return merchantService.saveMerchant(merchantEntity).map(merchantMapper::map);
    }

}
