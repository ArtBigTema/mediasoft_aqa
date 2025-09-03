package com.example.demo.rest.common;

import org.springframework.data.domain.Page;

import java.util.List;

public class Api {
    public static final String DESCRIPTION = "Данные получены";

    public static <T> Response.PositiveResponse<T> positiveResponse(T data) {
        if (data instanceof Iterable<?> iterable) {
            return new Response.PositiveResponse<>(data, !iterable.iterator().hasNext() ?
                    "Данные пустые" : DESCRIPTION); //todo to constant
        } else {
            return new Response.PositiveResponse<>(data, DESCRIPTION);
        }
    }

    public static <T> Response.PositiveResponse<List<T>> positiveResponse(Page<T> data) {
        return new Response.PositiveResponse<>(data.getContent(), DESCRIPTION).paged(data);
    }

    public static Response.NegativeResponse<String> negativeResponse(String code, String errorMessage, String desc, Object details) {
        return new Response.NegativeResponse<>(code, errorMessage, desc, details);
    }
}
