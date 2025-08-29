package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class Product extends AbstractEntity {
    @CreationTimestamp
    @JsonProperty(access = READ_ONLY)
    private LocalDateTime insertedAt;
    @UpdateTimestamp
    @JsonProperty(access = READ_ONLY)
    private LocalDateTime lastQtyChange;

    private Boolean isAvailable = Boolean.TRUE;
    @PositiveOrZero
    private BigDecimal qty;
    @Positive
    private BigDecimal price;
    @NotBlank
    private String name;
    private String article;
    private String dictionary;
    private String category;
}
