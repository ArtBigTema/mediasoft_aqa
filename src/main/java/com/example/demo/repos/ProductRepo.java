package com.example.demo.repos;

import com.example.demo.entity.Product;

public interface ProductRepo extends AbstractRepo<Product> {
    @Override
    default Class<Product> getClazz() {
        return Product.class;
    }
}
