package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.UUID;

import static com.example.demo.entity.OrderedProduct.Fields.orderId;
import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;
import static com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY;

@Data
@Entity
@Table(name = "\"order\"")
@EqualsAndHashCode(callSuper = true)
public class Order extends AbstractEntity {
    @JsonProperty(access = READ_ONLY)
    private String status;
    @NotNull
    private UUID customerId;
    @NotBlank
    private String deliveryAddress;
    @NotEmpty
    @Transient
    @JsonProperty(access = WRITE_ONLY)
    private List<Products> products;

    @OneToMany
    @JoinColumn(name = orderId)
    @JsonProperty(access = READ_ONLY)
    private List<OrderedProduct> orderedProducts;

    record Products(UUID id, int qty) {
    }
}
