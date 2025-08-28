package com.example.demo.rest.common.crud;

import com.example.demo.entity.AbstractEntity;
import com.example.demo.rest.common.Api;
import com.example.demo.rest.common.PositiveResponse;
import com.example.demo.service.CrudService;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@Validated
@RequestMapping
public class CrudController<E extends AbstractEntity> extends UpdateController<E> {
    public CrudController(CrudService crudService, Class<E> clazz) {
        super(crudService, clazz);
    }

    @PostMapping
    // @PreAuthorize("hasAnyRole('Администратор') || hasPermission(#this.this.clazz, #body)")
    public PositiveResponse<UUID> create(@RequestBody @Valid E body) {
        E saved = crudService.save(getClazz(), body);
        return Api.positiveResponse(saved.getId());
    }
}
