package com.example.demo.rest;

import com.example.demo.entity.Order;
import com.example.demo.rest.common.crud.CrudController;
import com.example.demo.service.CrudService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("orders")
public class OrderController extends CrudController<Order> {
    public OrderController(CrudService crudService) {
        super(crudService, Order.class);
    }
}
