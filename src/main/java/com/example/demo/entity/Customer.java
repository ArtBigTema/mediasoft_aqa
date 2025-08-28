package com.example.demo.entity;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class Customer extends AbstractEntity {
    private Boolean isActive;
    private String login;
    private String email;
}