package com.example.demo.rest.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NegativeResponse<T> extends Response {

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
