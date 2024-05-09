package com.geopokrovskiy.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table("callbacks")
public class CallbackEntity implements Persistable<UUID> {
    @Id
    private UUID id;
    @Column("transaction_id")
    private UUID transactionId;
    @Column
    private int iteration;

    @Override
    public boolean isNew() {
        return this.id == null;
    }

}
