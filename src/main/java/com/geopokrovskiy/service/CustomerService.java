package com.geopokrovskiy.service;

import com.geopokrovskiy.entity.CustomerEntity;
import com.geopokrovskiy.repository.CustomerRepository;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Data
@Slf4j
@Service
public class CustomerService {
    private final CustomerRepository customerRepository;

    public Mono<CustomerEntity> addNewCustomer(CustomerEntity customer) {
        return this.customerRepository.findByUserName(customer.getUserName())
                .switchIfEmpty(this.customerRepository.save(customer));
    }
}
