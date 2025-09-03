package com.example.demo.rest.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.data.domain.Page;

import java.io.Serializable;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Getter
@RequiredArgsConstructor
public abstract class Response implements Serializable {

    @Schema(description = "Признак успешного/фейлового ответа")
    private final Boolean result;
    @Schema(description = "Описание ответа")
    private final String description;


    @Getter
    @ToString
    public static class PositiveResponse<T> extends Response {

        @JsonInclude(NON_NULL)
        @Schema(description = "Контейнер с основной информацией ответа")
        private final T data;

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

    }
    @Data
    public static class PagingResults {
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

    @Getter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class NegativeResponse<T> extends Response {

        protected final T code;

        private final String message;

        private final Object details;

        NegativeResponse(T code, String message, String description, Object details) {
            super(Boolean.FALSE, description);
            this.code = code;
            this.message = message;
            this.details = details;
        }

        public Boolean getResult() {
            return Boolean.FALSE;
        }
    }
}
