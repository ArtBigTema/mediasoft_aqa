package com.example.demo.entity;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class Product extends AbstractEntity {
    @CreationTimestamp
    private LocalDateTime insertedAt;
    @UpdateTimestamp
    private LocalDateTime lastQtyChange;

    private Boolean isAvailable;
    private BigDecimal qty;
    private BigDecimal price;
    private String name;
    private String article;
    private String dictionary;
    private String category;
}