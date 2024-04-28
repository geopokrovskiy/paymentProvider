package com.geopokrovskiy.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Data

@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Table("merchants")
public class MerchantEntity implements Persistable<UUID> {
    @Column("id")
    @Id
    private UUID id;
    @Column
    private String username;
    @Column
    private String password;
    @Column("registration_date")
    private LocalDateTime registrationDate;
    @Column
    private String country;

    @ToString.Include(name = "password")
    private String maskPassword() {
        return "********";
    }

    @Override
    public boolean isNew() {
        return id == null;
    }
}
