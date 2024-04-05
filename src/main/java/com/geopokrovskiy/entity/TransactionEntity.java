package com.geopokrovskiy.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Table("transactions")
public class TransactionEntity {
    @Id
    @Column("id")
    private UUID uuid;
    @Column("transaction_type")
    private TransactionType transactionType;
    @Column("account_id")
    private UUID accountId;
    @Column("created_at")
    private LocalDateTime createdAt;
    @Column("updated_at")
    private LocalDateTime updatedAt;
    @Column("card_id")
    private UUID cardId;
    @Transient
    private CardEntity card;
    @Transient
    private CustomerEntity customer;
    @Column
    private String language;
    @Column
    private Double amount;
    @Column("notification_url")
    private String notificationURL;
    @Column("transaction_status")
    private TransactionStatus transactionStatus;
    @Transient
    @ToString.Exclude
    private List<CallbackEntity> callbackList;

}
