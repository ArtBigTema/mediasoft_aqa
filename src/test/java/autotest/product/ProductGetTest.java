package autotest.product;

import autotest.AuthTestCase;
import autotest.util.Constant;
import com.example.demo.entity.Product;
import com.example.demo.rest.common.Api;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import io.qameta.allure.Allure;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static autotest.util.Endpoints.PRODUCT_ENDPOINT;
import static java.math.BigInteger.TEN;
import static org.apache.commons.lang3.math.NumberUtils.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;

@Tags({@Tag("Product"), @Tag("get")})
public class ProductGetTest extends AuthTestCase {
    public static List<Object> ids = Collections.emptyList();
    public static final int FIVE = TEN.intValue() / INTEGER_TWO;
    public static final CollectionLikeType productListType = mapper
            .getTypeFactory().constructCollectionLikeType(ArrayList.class, Product.class);

    //    @BeforeAll
    public static void createProducts() {
        ids = Stream.generate(ProductCreateTest::createProduct)
                .limit(TEN.intValue()).toList();
    }

    //    @AfterAll
    public static void deleteCreatedProducts() {
        ids.forEach(i -> delete(PRODUCT_ENDPOINT + i, HttpStatus.OK.value()));
    }

    @Test
    @DisplayName("Простое получение данных о продуктах")
    public void testSimpleGetAllProducts() {
        get(PRODUCT_ENDPOINT)
                .body(Constant.DESCRIPTION_FIELD, equalTo(Api.DESCRIPTION))
                .body(Constant.RESULT_FIELD, equalTo(Boolean.TRUE))
                .body(Constant.DATA_FIELD, Matchers.notNullValue());
    }

    @Test
    @DisplayName("Простое получение данных о продуктах")
    public void testSimpleGetAllProductsByPositiveSize() {
        Allure.step("get all products on page with single element");
        int totalCount = getWithParams(PRODUCT_ENDPOINT, Map.of("size", INTEGER_ONE))
                .body("data", Matchers.iterableWithSize(INTEGER_ONE))
                .body("pagingResults.size", equalTo(INTEGER_ONE))
                .body("pagingResults.number", equalTo(INTEGER_ZERO))
                .body("pagingResults.totalCount", greaterThan(TEN.intValue()))
                .extract().path("pagingResults.totalCount");

        Allure.step("get all products on full page");
        getWithParams(PRODUCT_ENDPOINT, Map.of("size", totalCount))
                .body("data", Matchers.iterableWithSize(totalCount))
                .body("pagingResults.size", equalTo(totalCount))
                .body("pagingResults.number", equalTo(INTEGER_ZERO))
                .body("pagingResults.totalCount", equalTo(totalCount));

        Allure.step("get all products on second page");
        getWithParams(PRODUCT_ENDPOINT, Map.of("size", INTEGER_ONE, "page", INTEGER_TWO))
                .body("data", Matchers.iterableWithSize(INTEGER_ONE))
                .body("pagingResults.size", equalTo(INTEGER_ONE))
                .body("pagingResults.number", equalTo(INTEGER_TWO))
                .body("pagingResults.totalCount", equalTo(totalCount));

        Allure.step("get all products on last page");
        getWithParams(PRODUCT_ENDPOINT, Map.of("size", INTEGER_ONE, "page", totalCount - INTEGER_ONE))
                .body("pagingResults.size", equalTo(INTEGER_ONE))
                .body("pagingResults.number", equalTo(totalCount - INTEGER_ONE))
                .body("pagingResults.totalCount", equalTo(totalCount));

        Allure.step("get all products on over page");
        getWithParams(PRODUCT_ENDPOINT, Map.of("size", INTEGER_ONE, "page", totalCount))
                .body("data", Matchers.iterableWithSize(INTEGER_ZERO))
                .body("pagingResults.size", equalTo(INTEGER_ONE))
                .body("pagingResults.number", equalTo(totalCount))
                .body("pagingResults.totalCount", equalTo(totalCount));
    }

    @Test
    @DisplayName("Простое получение данных о продуктах")
    public void testSimpleGetAllProductsBySort() {
        List<Product> list = getWithParams(PRODUCT_ENDPOINT, Map.of("size", FIVE,
                "sort", Product.Fields.insertedAt + ",desc"
        )).extract().jsonPath().getList("data", Product.class);
    }

}
