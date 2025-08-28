package com.example.demo.repos;

import com.example.demo.entity.Order;

public interface OrderRepo extends AbstractRepo<Order> {
    @Override
    default Class<Order> getClazz() {
        return Order.class;
    }
}
