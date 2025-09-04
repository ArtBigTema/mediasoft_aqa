package com.example.demo.entity;

import com.example.demo.rest.common.Errors;
import com.example.demo.service.CrudService;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;
import org.apache.commons.lang3.math.NumberUtils;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;
import static com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY;
import static jakarta.persistence.CascadeType.ALL;

@Data
@Entity
@FieldNameConstants
@Accessors(chain = true)
@Table(name = "\"order\"")
@EqualsAndHashCode(callSuper = true)
public class Order extends AbstractEntity {
    @JsonProperty(access = READ_ONLY)
    private String status;
    @NotNull
    private UUID customerId;
    @NotBlank
    @Length(max = 255)
    private String deliveryAddress;
    @NotEmpty
    @Transient
    @JsonProperty(access = WRITE_ONLY)
    private List<Products> products;

    @OneToMany(cascade = ALL)
    @JoinColumn(name = "orderId")
    @JsonProperty(access = READ_ONLY)
    private List<OrderedProduct> orderedProducts;

    @Override
    public void onCreate(CrudService crudService) {
        super.onCreate(crudService);
        crudService.exist(Customer.class, getCustomerId());

        Set<UUID> productIds = products.stream().map(Products::getId).collect(Collectors.toSet());
        Map<UUID, Product> productMap = crudService.findAll(Product.class, productIds);

        List<OrderedProduct> productList = products
                .stream().map(p -> {
                    OrderedProduct o = new OrderedProduct();
                    o.setOrder(this);
                    o.setQty(p.getQty());
                    Product product = productMap.get(p.getId());
                    Errors.E506.thr(product.getIsAvailable());
                    product.setQty(product.getQty().subtract(p.getQty()));
                    Errors.E505.thrIf(product.getQty().compareTo(BigDecimal.ZERO) < NumberUtils.INTEGER_ZERO);
                    o.setPrice(p.getQty().multiply(product.getPrice()));
                    o.setProduct(product);
                    return o;
                }).toList();

        setOrderedProducts(productList);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Products {
        UUID id;
        BigDecimal qty;
    }
}
