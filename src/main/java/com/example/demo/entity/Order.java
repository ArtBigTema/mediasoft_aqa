package com.example.demo.entity;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class Order extends AbstractEntity {
    private Long customerId;
    private String status;
    private String deliveryAddress;
}