package autotest.order;

import autotest.AuthTestCase;
import autotest.util.Random;
import com.example.demo.entity.Order;
import com.example.demo.entity.Product;
import io.qameta.allure.Step;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static autotest.util.Constant.AUTO_TEST_CATEGORY;
import static autotest.util.Constant.ID_DATA_FIELD;
import static autotest.util.Endpoints.*;
import static java.math.BigInteger.TEN;
import static org.apache.commons.lang3.math.NumberUtils.INTEGER_TWO;

@SuppressWarnings("unchecked")
@Tags({@Tag("Order"), @Tag("post")})
public class OrderCreateTest extends AuthTestCase {
    public static List<Object> ids = Collections.emptyList();
    public static final int FIVE = TEN.intValue() / INTEGER_TWO;
    public static UUID customerId = UUID.randomUUID();
    public static UUID productId = UUID.randomUUID();

    @BeforeAll
    public static void saveCustomerId() {
        customerId = get(CUSTOMER_ENDPOINT).extract().body()
                .jsonPath().getList(ID_DATA_FIELD, UUID.class)
                .getFirst();
        productId = getWithParams(PRODUCT_ENDPOINT,
                Map.of(Product.Fields.isAvailable, Boolean.TRUE,
                        Product.Fields.category, AUTO_TEST_CATEGORY))
                .extract().body()
                .jsonPath().getList(ID_DATA_FIELD, UUID.class)
                .getFirst();
    }

    @Step("save created random order")
    public static Object createAndSaveOrder() {
        return post(ORDER_ENDPOINT, createCorrectOrder())
                .extract().body().path(ID_DATA_FIELD);
    }

    @Step("create random order")
    public static Order createCorrectOrder() {
        return Random.getInstance().nextObject(Order.class)
                .setDeliveryAddress(AUTO_TEST_CATEGORY)
                .setCustomerId(customerId)
                .setProducts(Collections.singletonList(
                        new Order.Products(productId, BigDecimal.ONE)));
    }

    @Test
    @DisplayName("Простое получение данных о заказах")
    public void testSimpleGetAllProducts() {
        Object id = post(ORDER_ENDPOINT, createCorrectOrder())
                .extract().path(ID_DATA_FIELD);

        delete(ORDER_ENDPOINT + id, 200);
    }

}
