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
@Table("merchants")
public class MerchantEntity {
    @Column("id")
    @Id
    private UUID uuid;
    @Column
    private String username;
    @Column
    private String password;
    @Column("registration_date")
    private LocalDateTime registrationDate;
    @Column
    private String country;
    @Transient
    @ToString.Exclude
    private List<AccountEntity> accountList;

    @ToString.Include(name = "password")
    private String maskPassword() {
        return "********";
    }
}
