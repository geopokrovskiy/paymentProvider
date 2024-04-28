package com.geopokrovskiy.controller;

import com.geopokrovskiy.dto.merchant.MerchantDto;
import com.geopokrovskiy.dto.merchant.MerchantResponseDto;
import com.geopokrovskiy.entity.MerchantEntity;
import com.geopokrovskiy.mapper.merchant.MerchantMapper;
import com.geopokrovskiy.mapper.merchant.MerchantResponseMapper;
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
    private final MerchantResponseMapper merchantResponseMapper;

    @PostMapping("/register")
    public Mono<MerchantResponseDto> addMerchant(@RequestBody MerchantDto merchantDto) {
        MerchantEntity merchantEntity = merchantMapper.map(merchantDto);
        return merchantService.saveMerchant(merchantEntity).map(merchantResponseMapper::map);
    }

}
