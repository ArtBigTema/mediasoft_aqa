package com.example.demo.rest.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.data.domain.Page;

@Data
public class PagingResults {
    @Schema(description = "Номер страницы")
    private Integer number;
    @Schema(description = "Общее количество страниц")
    private Integer total;
    @Schema(description = "Размер страницы в выборке")
    private Integer size;
    @Schema(description = "Общее количество элементов в выборке")
    private Long totalCount;

    public static PagingResults build(Page<?> page) {
        PagingResults p = new PagingResults();
        p.setTotalCount(page.getTotalElements());
        p.setTotal(page.getTotalPages());
        p.setNumber(page.getNumber());
        p.setSize(page.getSize());
        return p;
    }
}
