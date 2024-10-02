package com.example.ticketdestroyer.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;


import java.math.BigDecimal;

@Entity
@Setter
@Getter
public class Discount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;

    private Integer quantityLeft;

    private BigDecimal percentage; // Use BigDecimal for monetary values
}
