package com.example.demo.rest.common.crud;

import com.example.demo.entity.AbstractEntity;
import com.example.demo.rest.common.Api;
import com.example.demo.rest.common.PositiveResponse;
import com.example.demo.service.CrudService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Validated
@RequestMapping
public class UpdateController<E extends AbstractEntity> extends ReadableController<E> {

    public UpdateController(CrudService crudService, Class<E> clazz) {
        super(crudService, clazz);
    }

    @PatchMapping("{id}")
    public PositiveResponse<UUID> update(@PathVariable UUID id, @RequestBody E body) {
        E updated = crudService.update(id, body);
        return Api.positiveResponse(updated.getId());
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable UUID id) {
        crudService.delete(id, getClazz());
    }
}
