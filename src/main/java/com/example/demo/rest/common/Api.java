package com.example.demo.rest.common;

import org.springframework.data.domain.Page;

import java.util.List;

public class Api {
    private static final String DESCRIPTION = "Данные получены";

    public static <T> PositiveResponse<T> positiveResponse(T data) {
        if (data instanceof Iterable<?> iterable) {
            return new PositiveResponse<>(data, !iterable.iterator().hasNext() ?
                    "Данные пустые" : DESCRIPTION); //todo to constant
        } else {
            return new PositiveResponse<>(data, DESCRIPTION);
        }
    }

    public static <T> PositiveResponse<List<T>> positiveResponse(Page<T> data) {
        return new PositiveResponse<>(data.getContent(), DESCRIPTION).paged(data);
    }

    public static NegativeResponse<String> negativeResponse(String code, String errorMessage, Object details) {
        return new NegativeResponse<>(code, errorMessage, "Smth went wrong", details);
    }
}
