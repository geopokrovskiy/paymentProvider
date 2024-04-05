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
@Table("customers")
public class CustomerEntity {
    @Id
    @Column("id")
    private UUID uuid;
    @Column("first_name")
    private String firstName;
    @Column("last_name")
    private String lastName;
    @Column
    private String country;
    @Transient
    @ToString.Exclude
    private List<CardEntity> cardList;
}
