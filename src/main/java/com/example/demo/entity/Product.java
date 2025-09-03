package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;
import org.apache.commons.lang3.math.NumberUtils;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.example.demo.util.Utils.ISO_DATE_TIME;
import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;
import static org.apache.commons.lang3.math.NumberUtils.*;

@Data
@Entity
@FieldNameConstants
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class Product extends AbstractEntity {
    @CreationTimestamp
    @JsonProperty(access = READ_ONLY)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ISO_DATE_TIME)
    private LocalDateTime insertedAt;
    @UpdateTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ISO_DATE_TIME)
    @JsonProperty(access = READ_ONLY)
    private LocalDateTime lastQtyChange;

    private Boolean isAvailable = Boolean.TRUE;
    @NotNull
    @PositiveOrZero
    @Digits(integer = Integer.MAX_VALUE, fraction = 2)
    private BigDecimal qty;
    @NotNull
    @Positive
    @Digits(integer = Integer.MAX_VALUE, fraction = 2)
    private BigDecimal price;
    @NotBlank
    private String name;
    private String article;
    private String dictionary;
    private String category;
}
