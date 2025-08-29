package com.example.demo.rest;

import com.example.demo.entity.Order;
import com.example.demo.rest.common.PositiveResponse;
import com.example.demo.rest.common.crud.CrudController;
import com.example.demo.service.CrudService;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("orders")
public class OrderController extends CrudController<Order> {
    public OrderController(CrudService crudService) {
        super(crudService, Order.class);
    }

    @Hidden
    @Override
    public PositiveResponse<List<Order>> getAll(Pageable pageable, Map<String, Object> params) {
        throw new UnsupportedOperationException("Operation not supported");
    }
}
