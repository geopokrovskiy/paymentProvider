package com.geopokrovskiy.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Table("accounts")
public class AccountEntity {
    @Id
    @Column("id")
    private UUID uuid;
    @Column("currency_code")
    private String currencyCode;
    @Column("merchant_id")
    private UUID merchantId;
    @Column
    private Long balance;
    @Transient
    @ToString.Exclude
    private List<TransactionEntity> transactionList;
}
