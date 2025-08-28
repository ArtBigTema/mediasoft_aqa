package com.example.demo.rest.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@Getter
@RequiredArgsConstructor
public abstract class Response implements Serializable {

    @Schema(description = "Признак успешного/фейлового ответа")
    private final Boolean result;
    @Schema(description = "Описание ответа")
    private final String description;
}
