package com.example.demo.rest;

import com.example.demo.entity.Product;
import com.example.demo.rest.common.crud.CrudController;
import com.example.demo.service.CrudService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("products")
public class ProductController extends CrudController<Product> {
    public ProductController(CrudService crudService) {
        super(crudService, Product.class);
    }
}
