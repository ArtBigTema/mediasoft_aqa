package com.example.demo.repos;

import com.example.demo.entity.Customer;

public interface CustomerRepo extends AbstractRepo<Customer> {
    @Override
    default Class<Customer> getClazz() {
        return Customer.class;
    }
}
