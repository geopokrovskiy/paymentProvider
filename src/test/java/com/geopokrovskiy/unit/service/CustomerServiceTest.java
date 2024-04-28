package com.geopokrovskiy.unit.service;

import com.geopokrovskiy.entity.CustomerEntity;
import com.geopokrovskiy.repository.CustomerRepository;
import com.geopokrovskiy.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.mockito.Mockito.*;

@SpringBootTest
public class CustomerServiceTest {
    private final CustomerRepository customerRepository = Mockito.mock(CustomerRepository.class);
    @InjectMocks
    private CustomerService customerService;
    private final String USERNAME = "USERNAME_1";

    @Test
    public void testAddNewCustomer() {
        CustomerEntity expectedCustomer = this.getExpectedCustomer();
        CustomerEntity newCustomer = this.getCustomerWithoutId();

        Mockito.when(customerRepository.findByUserName(USERNAME)).thenReturn(Mono.just(expectedCustomer));
        Mockito.when(customerRepository.save(newCustomer)).thenReturn(Mono.just(expectedCustomer));

        Mono<CustomerEntity> result = this.customerService.addNewCustomer(newCustomer);

        result.subscribe(
                res -> {
                    verify(customerRepository, times(1)).findByUserName(USERNAME);
                    verify(customerRepository, times(0)).save(any());
                }
        );
    }

    private CustomerEntity getExpectedCustomer() {
        return new CustomerEntity().toBuilder()
                .country("Country1")
                .firstName("fn1")
                .lastName("ln1")
                .userName(USERNAME)
                .id(UUID.randomUUID())
                .build();
    }

    private CustomerEntity getCustomerWithoutId() {
        return new CustomerEntity().toBuilder()
                .country("Country1")
                .firstName("fn1")
                .lastName("ln1")
                .userName(USERNAME)
                .build();
    }


}
