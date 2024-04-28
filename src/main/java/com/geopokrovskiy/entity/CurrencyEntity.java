package com.geopokrovskiy.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Table("currencies")
public class CurrencyEntity {
    @Column("currency_code")
    @Id
    private String currencyCode;
    @Transient
    @ToString.Exclude
    private List<AccountEntity> accountList;
}
