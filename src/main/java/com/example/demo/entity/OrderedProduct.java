package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;

import java.lang.reflect.Field;
import java.math.BigDecimal;

@Data
@Entity
@FieldNameConstants
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class OrderedProduct extends AbstractEntity {
    @NotNull
    @ManyToOne
    @JsonIgnore
//    @JoinColumn(name="orderId")
    private Order order;
    @NotNull
    @ManyToOne
//    @JoinColumn(name="productId")
    @JsonIgnore
    private Product product;

    private BigDecimal qty;
    private BigDecimal price;
}
