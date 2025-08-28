package com.example.demo.rest.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.domain.Page;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@ToString
public class PositiveResponse<T> extends Response {

    @JsonInclude(NON_NULL)
    @Schema(description = "Контейнер с основной информацией ответа")
    private final T data;

    @Getter
    @JsonInclude(NON_NULL)
    @Schema(description = "Параметры пагинации")
    private PagingResults pagingResults;

    public PositiveResponse(T data, String description) {
        super(Boolean.TRUE, description);
        this.data = data;
    }

    public PositiveResponse<T> paged(Page<?> data) {
        this.pagingResults = PagingResults.build(data);
        return this;
    }

    public Boolean getResult() {
        return Boolean.TRUE;
    }

    public T getData() {
        return data;
    }
}
