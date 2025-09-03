package autotest.product;

import autotest.AuthTestCase;
import autotest.util.Random;
import com.example.demo.entity.Product;
import com.example.demo.rest.common.Errors;
import com.example.demo.util.Utils;
import com.google.common.collect.Maps;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import io.restassured.http.Method;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static autotest.util.Constant.*;
import static autotest.util.Endpoints.PRODUCT_ENDPOINT;

@Tags({@Tag("Product"), @Tag("post")})
public class ProductCreateTest extends AuthTestCase {

    public static Object createProduct() {
        return createAndSaveProductWithCategory(AUTO_TEST_CATEGORY);
    }

    @Step("save created random product")
    public static Object createAndSaveProductWithCategory(String category) {
        return post(PRODUCT_ENDPOINT, createProductWithCategory(category))
                .extract().body().path(ID_DATA_FIELD);
    }

    @Step("create random product")
    public static Product createProductWithCategory(String category) {
        return Random.getInstance().nextObject(Product.class).setCategory(category);
    }

    @Test
    @DisplayName("Валидация создания продукта с негативными сценариями")
    public void testSimpleCreateProductNegative() {
        List<Function<Product, Product>> list = Arrays.asList(
                r -> r.setPrice(BigDecimal.valueOf(0.001)),
                r -> r.setQty(BigDecimal.valueOf(0.001)),
                r -> r.setQty(BigDecimal.valueOf(NumberUtils.INTEGER_MINUS_ONE)),
                r -> r.setPrice(BigDecimal.valueOf(NumberUtils.INTEGER_MINUS_ONE)),
                r -> r.setPrice(BigDecimal.valueOf(NumberUtils.INTEGER_ZERO)),
                r -> r.setName(StringUtils.EMPTY),
                r -> r.setName(StringUtils.SPACE.repeat(NumberUtils.INTEGER_TWO)),
                r -> r.setPrice(null), r -> r.setQty(null), r -> r.setName(null)
        );
        list.stream().map(c -> c.apply(createProductWithCategory(AUTO_TEST_CATEGORY)))
                .forEach(p -> post(PRODUCT_ENDPOINT, p, Errors.E101));
    }

    @Test
    @DisplayName("Валидация успешно созданного продукта")
    public void testSimpleCreateProductPositive() {
        Product product = createProductWithCategory(AUTO_TEST_CATEGORY);
        Allure.step("create product");
        Object id = post(PRODUCT_ENDPOINT, product).extract().path(ID_DATA_FIELD);
        String now = Utils.FORMAT.format(LocalDateTime.now());
        Allure.step("check created product" + id);
        Map<?, ?> result = get(PRODUCT_ENDPOINT + id)
                .body(ID_DATA_FIELD, Matchers.notNullValue())
                .body("data." + Product.Fields.insertedAt, Matchers.greaterThanOrEqualTo(now))
                .extract().jsonPath().getObject("data", Map.class);
        Map<?, ?> map = mapper.convertValue(product, Map.class);

        extractAndCheck(id, map, result);
        Allure.step("delete created product " + id);
        delete(PRODUCT_ENDPOINT + id, HttpStatus.OK.value());
    }

    @Step("check")
    private  void extractAndCheck(Object id, Map<?, ?> map, Map<?, ?> result, Object... extraField) {
        Allure.step("remove empty fields for product" + id);
        Set<Object> emptyFields = map.entrySet().stream().filter(e -> ObjectUtils.isEmpty(e.getValue()))
                .map(Map.Entry::getKey).collect(Collectors.toSet()); // fields on create

        emptyFields.addAll(Arrays.asList(extraField));
        map.keySet().removeIf(emptyFields::contains);
        result.keySet().removeIf(emptyFields::contains);

        Allure.step("check fields for product" + id);
        Assertions.assertIterableEquals(
                new TreeMap<>(Maps.transformValues(result, String::valueOf)).entrySet(),
                new TreeMap<>(Maps.transformValues(map, String::valueOf)).entrySet()
        );
    }

    @SuppressWarnings("unchecked")
    @Test
    @Tag("update")
    @DisplayName("Валидация изменения созданного продукта")
    public void testSimpleUpdateProductPositive() {
        Product product = createProductWithCategory(AUTO_TEST_CATEGORY);
        Allure.step("create product");
        Object id = post(PRODUCT_ENDPOINT, product).extract().path(ID_DATA_FIELD);
        String now = Utils.FORMAT.format(LocalDateTime.now().minusSeconds(NumberUtils.INTEGER_ONE));

        Allure.step("check created product" + id);
        Utils.safetyTake(() -> Thread.sleep(MAX_TIMEOUT));
        Map<Object, Object> result = get(PRODUCT_ENDPOINT + id)
                .body(ID_DATA_FIELD, Matchers.notNullValue())
                .body("data." + Product.Fields.insertedAt, Matchers.greaterThanOrEqualTo(now))
                .extract().jsonPath().getObject("data", Map.class);

        List<Pair<String, ?>> list = Arrays.asList(
                Pair.of(Product.Fields.name, "smthName"),
                Pair.of(Product.Fields.article, "smthArticle"),
                Pair.of(Product.Fields.category, "smthCategory"),
                Pair.of(Product.Fields.dictionary, "smthDictionary"),
                Pair.of(Product.Fields.isAvailable, !product.getIsAvailable()),
                Pair.of(Product.Fields.qty, 10.01F),
                Pair.of(Product.Fields.price, 10.01F)
        );
        for (Pair<?, ?> pair : list) {
            Map<Object, Object> prev = new HashMap<>(result);
            Allure.step("reverse isAvailable field for product " + id);

            now = Utils.FORMAT.format(LocalDateTime.now());
            Utils.safetyTake(() -> Thread.sleep(MAX_TIMEOUT));
            Allure.step("check patch created product" + id + " field: " + pair);
            result.put(pair.getKey(), pair.getValue());
            execute(PRODUCT_ENDPOINT + id, HttpStatus.OK.value(), Method.PATCH, result);
            Map<?, ?> data = get(PRODUCT_ENDPOINT + id)
                    .body("data." + Product.Fields.insertedAt, Matchers.equalTo(result.get(Product.Fields.insertedAt)))
                    .body("data." + Product.Fields.lastQtyChange, Matchers.greaterThanOrEqualTo(now))
                    .body("data." + pair.getKey(), Matchers.equalTo(pair.getValue()))
                    .extract().jsonPath().getObject("data", Map.class);

            Assertions.assertNotEquals(prev.get(pair.getKey()), data.get(pair.getKey()));
            extractAndCheck(id, prev, data, Product.Fields.lastQtyChange, pair.getKey());
        }


        delete(PRODUCT_ENDPOINT + id, HttpStatus.OK.value());
    }
}
