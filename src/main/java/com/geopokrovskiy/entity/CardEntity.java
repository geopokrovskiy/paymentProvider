package com.geopokrovskiy.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Table("cards")
public class CardEntity implements Persistable<UUID> {
    @Id
    @Column("id")
    private UUID id;
    @Column("customer_id")
    private UUID customerId;
    @Column("card_number")
    private String cardNumber;
    @Column
    private String cvv;
    @Column("expiration_date")
    private String expirationDate;
    @Transient
    @ToString.Exclude
    private List<TransactionEntity> transactionList;
    @Override
    public boolean isNew() {
        return id == null;
    }
}
