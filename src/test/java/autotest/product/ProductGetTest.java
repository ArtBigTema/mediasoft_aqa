package autotest.product;

import autotest.AuthTestCase;
import autotest.util.Constant;
import com.example.demo.entity.Product;
import com.example.demo.rest.common.Api;
import com.example.demo.rest.common.Errors;
import com.example.demo.util.Utils;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import com.google.common.collect.Maps;
import io.qameta.allure.Allure;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static autotest.product.ProductCreateTest.createProductWithCategory;
import static autotest.util.Constant.*;
import static autotest.util.Endpoints.PRODUCT_ENDPOINT;
import static com.example.demo.util.Utils.*;
import static java.math.BigInteger.TEN;
import static org.apache.commons.lang3.math.NumberUtils.*;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.*;

@SuppressWarnings({"unchecked","rawtypes"})
@Tags({@Tag("Product"), @Tag("get")})
public class ProductGetTest extends AuthTestCase {
    public static List<Object> ids = Collections.emptyList();
    public static final int FIVE = TEN.intValue() / INTEGER_TWO;

     @BeforeAll
    public static void createProducts() {
        ids = Stream.generate(ProductCreateTest::createProduct)
                .limit(TEN.intValue()).toList();
    }

      @AfterAll
    public static void deleteCreatedProducts() {
        ids.forEach(i -> delete(PRODUCT_ENDPOINT + i, HttpStatus.OK.value()));
    }

    @Test
    @DisplayName("Простое получение данных о продуктах")
    public void testSimpleGetAllProducts() {
        get(PRODUCT_ENDPOINT)
                .body(Constant.DESCRIPTION_FIELD, equalTo(Api.DESCRIPTION))
                .body(Constant.RESULT_FIELD, equalTo(Boolean.TRUE))
                .body(DATA_FIELD, Matchers.notNullValue());
    }

    @Test
    @Tag("paging")
    @DisplayName("Негативные сценарии агинации при получении данных о продуктах")
    public void testSimpleGetAllProductsByNegative() {
        List<?> list = Arrays.asList( // fixme mb to dataProvider
                Utils.SIZE_PARAMETER, INTEGER_ZERO,
                Utils.SIZE_PARAMETER, INTEGER_MINUS_ONE,
                PAGE_PARAMETER, INTEGER_MINUS_ONE,
                SORT_PARAMETER, RandomStringUtils.insecure().nextAlphabetic(INTEGER_TWO)
        );

        for (int i = INTEGER_ZERO; i < list.size(); i += INTEGER_TWO) {
            Object field = list.get(i);
            Object value = list.get(i + INTEGER_ONE);
            Allure.step(String.format("get all products with field %s and value %s", field, value));
            getWithError(PRODUCT_ENDPOINT, Map.of(field.toString(), value), Errors.E700);
        }
    }

    @Test
    @Tag("paging")
    @DisplayName("Позитивные сценарии агинации при получении данных о продуктах")
    public void testSimpleGetAllProductsByPositive() {
        Allure.step("get all products on page with single element");
        int totalCount = getWithParams(PRODUCT_ENDPOINT, Map.of(SIZE_PARAMETER, INTEGER_ONE))
                .body(DATA_FIELD, Matchers.iterableWithSize(INTEGER_ONE))
                .body("pagingResults.size", equalTo(INTEGER_ONE))
                .body("pagingResults.number", equalTo(INTEGER_ZERO))
                .body("pagingResults.totalCount", greaterThan(TEN.intValue()))
                .extract().path("pagingResults.totalCount");

        Allure.step("get all products on full page");
        getWithParams(PRODUCT_ENDPOINT, Map.of(SIZE_PARAMETER, totalCount))
                .body(DATA_FIELD, Matchers.iterableWithSize(totalCount))
                .body("pagingResults.size", equalTo(totalCount))
                .body("pagingResults.number", equalTo(INTEGER_ZERO))
                .body("pagingResults.totalCount", equalTo(totalCount));

        Allure.step("get all products on second page");
        getWithParams(PRODUCT_ENDPOINT, Map.of("size", INTEGER_ONE, PAGE_PARAMETER, INTEGER_TWO))
                .body(DATA_FIELD, Matchers.iterableWithSize(INTEGER_ONE))
                .body("pagingResults.size", equalTo(INTEGER_ONE))
                .body("pagingResults.number", equalTo(INTEGER_TWO))
                .body("pagingResults.totalCount", equalTo(totalCount));

        Allure.step("get all products on last page");
        getWithParams(PRODUCT_ENDPOINT, Map.of("size", INTEGER_ONE, PAGE_PARAMETER, totalCount - INTEGER_ONE))
                .body("pagingResults.size", equalTo(INTEGER_ONE))
                .body("pagingResults.number", equalTo(totalCount - INTEGER_ONE))
                .body("pagingResults.totalCount", equalTo(totalCount));

        Allure.step("get all products on over page");
        getWithParams(PRODUCT_ENDPOINT, Map.of(SIZE_PARAMETER, INTEGER_ONE, PAGE_PARAMETER, totalCount))
                .body(DATA_FIELD, Matchers.iterableWithSize(INTEGER_ZERO))
                .body("pagingResults.size", equalTo(INTEGER_ONE))
                .body("pagingResults.number", equalTo(totalCount))
                .body("pagingResults.totalCount", equalTo(totalCount));
    }

    @Test
    @Tag("sorting")
    @DisplayName("Простое получение данных о продуктах с сортировкой по каждому полю")
    public void testSimpleGetAllProductsBySort() {
        Set<String> fields = Arrays.stream(FieldUtils.getAllFields(Product.class))
                .map(Field::getName).collect(Collectors.toSet());
        fields.remove(MAP_FIELD);

        for (Sort.Direction direction : Sort.Direction.values()) {
            for (String field : fields) {
                Allure.step("check sort by " + field + " " + direction);

                Comparator<Map> comparing = mapComparator(field);
                if (direction.isDescending()) {
                    comparing = Collections.reverseOrder(comparing);
                }

                List<Map> maps = getWithParams(PRODUCT_ENDPOINT,
                        Map.of(SIZE_PARAMETER, FIVE, SORT_PARAMETER, field + "," + direction)
                ).extract().jsonPath().getList(DATA_FIELD, Map.class);

                List<Map> sorted = maps.stream().sorted(comparing).toList();
                Assertions.assertIterableEquals(maps, sorted);
            }
        }
    }

    @Test
    @Tag("filtering")
    @DisplayName("Простое получение данных о продуктах с сортировкой по каждому полю")
    public void testSimpleGetAllProductsByFilter() {
        Product product = createProductWithCategory(AUTO_TEST_CATEGORY);
        Map response = MapUtils.getMap(post(PRODUCT_ENDPOINT, product)
                .extract().body().as(Map.class), DATA_FIELD);
        Map map = getWithParams(PRODUCT_ENDPOINT, response)
                .extract().jsonPath().getList(DATA_FIELD, Map.class).getFirst();

        Allure.step("check filter by all fields");
        Assertions.assertIterableEquals( // не понятно почему в первом Double, во втором Float
                Maps.transformValues(response, String::valueOf).entrySet(),
                Maps.transformValues(map, String::valueOf).entrySet()
        );
        for (Object key : response.keySet()) {
            map = Map.of(key, response.get(key));

            Allure.step("check filter by single field and full value: " + map);
            getWithParams(PRODUCT_ENDPOINT, map)
                    .body(DATA_FIELD, Matchers.iterableWithSize(greaterThanOrEqualTo(INTEGER_ONE)))
                    .body("pagingResults.totalCount", greaterThanOrEqualTo(INTEGER_ONE))
                    .body("data", hasItem(hasEntry("id", response.get("id"))));
        }
        delete(PRODUCT_ENDPOINT + response.get("id"), SC_OK);
    }

}
