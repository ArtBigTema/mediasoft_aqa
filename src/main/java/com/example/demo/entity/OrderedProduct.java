package com.example.demo.entity;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class OrderedProduct extends AbstractEntity {
    @NotNull
    private UUID orderId;
    @NotNull
    private UUID productId;
    private BigDecimal qty;
    private BigDecimal price;
}