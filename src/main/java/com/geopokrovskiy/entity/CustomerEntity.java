package com.geopokrovskiy.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Table("customers")
public class CustomerEntity implements Persistable<UUID> {
    @Id
    @Column("id")
    private UUID id;
    @Column("first_name")
    private String firstName;
    @Column("last_name")
    private String lastName;
    @Column("country")
    private String country;
    @Column("username")
    private String userName;
    @Override
    public boolean isNew() {
        return id == null;
    }
}
