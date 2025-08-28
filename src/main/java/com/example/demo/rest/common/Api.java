package com.example.demo.rest.common;

public class Api {
    public static PositiveResponse<String> descriptedResponse(String description) {
        return new PositiveResponse<>("ok", description);
    }

    public static <T> PositiveResponse<T> positiveResponse(T data) {
        if (data instanceof Iterable<?> iterable) {
            return new PositiveResponse<>(data, !iterable.iterator().hasNext() ?
                    "Данные пустые" : "Данные получены"); //todo to constant
        } else {
            return new PositiveResponse<>(data, "Данные получены");
        }
    }

    public static NegativeResponse<String> negativeResponse(String code, String errorMessage, Object details) {
        return new NegativeResponse<>(code, errorMessage, "Smth went wrong", details);
    }

    public static NegativeResponse<String> negativeResponse(String code, String errorMessage, String description, Object details) {
        return new NegativeResponse<>(code, errorMessage, description, details);
    }
}
