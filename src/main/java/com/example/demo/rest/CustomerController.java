package com.example.demo.rest;

import com.example.demo.entity.Customer;
import com.example.demo.entity.Product;
import com.example.demo.rest.common.crud.CrudController;
import com.example.demo.rest.common.crud.ReadableController;
import com.example.demo.service.CrudService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("customers")
public class CustomerController extends ReadableController<Customer> {
    public CustomerController(CrudService crudService) {
        super(crudService, Customer.class);
    }
}
