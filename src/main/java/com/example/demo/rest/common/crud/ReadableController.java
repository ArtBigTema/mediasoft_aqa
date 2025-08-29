package com.example.demo.rest.common.crud;

import com.example.demo.entity.AbstractEntity;
import com.example.demo.rest.common.Api;
import com.example.demo.rest.common.PositiveResponse;
import com.example.demo.service.CrudService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.UUID;


@Validated
@RequestMapping
@RequiredArgsConstructor
public class ReadableController<E extends AbstractEntity> {
    protected final CrudService crudService;
    @Getter
    private final Class<E> clazz;

    @GetMapping("{id}")
    public PositiveResponse<E> getById(@PathVariable UUID id) {
        return Api.positiveResponse(crudService.find(clazz, id));
    }

    @GetMapping
    @Parameters({
            @Parameter(schema = @Schema(type = "integer"), name = "page"),
            @Parameter(schema = @Schema(type = "integer"), name = "size"),
            @Parameter(schema = @Schema(type = "string"), name = "searchString"),
            @Parameter(schema = @Schema(type = "string"), name = "sort", example = "id,desc")
    })
    public PositiveResponse<List<E>> getAll(@SortDefault(sort = "id", direction = Sort.Direction.DESC)
                                            @Parameter(hidden = true) @PageableDefault Pageable pageable,
                                            @RequestParam(required = false) Map<String, Object> params) {
        return Api.positiveResponse(crudService.findAll(clazz, params, pageable));
    }


}
