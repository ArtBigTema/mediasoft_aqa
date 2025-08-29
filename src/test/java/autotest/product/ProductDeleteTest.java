package autotest.product;

import autotest.AuthTestCase;
import com.example.demo.rest.common.Errors;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static autotest.product.ProductCreateTest.createProduct;
import static autotest.util.Endpoints.PRODUCT_ENDPOINT;
import static org.hamcrest.Matchers.equalTo;


@Tags({@Tag("Product"), @Tag("delete")})
public class ProductDeleteTest extends AuthTestCase {
    @Test
    @DisplayName("Валидация удаления продукта негативные сценарии")
    public void testSimpleDeleteProductNegate() {
        Allure.step("delete all products ");
        delete(PRODUCT_ENDPOINT, HttpStatus.METHOD_NOT_ALLOWED.value());

        List<String> ids = Arrays.asList("id", "1", "0", "-1", "abc", "\u206E",
                UUID.randomUUID().toString().substring(10));

        for (String id : ids) {
            Allure.step("delete product with id " + id);
            delete(PRODUCT_ENDPOINT + id, Errors.E700);
        }
    }

    @Test
    @DisplayName("Валидация удаления созданного продукта")
    public void testSimpleDeleteProductPositive() {
        Object id = createProduct();
        String endpoint = PRODUCT_ENDPOINT + id;

        Allure.step("read created product with id " + id);
        get(endpoint).body("data.id", equalTo(id));

        Allure.step("delete created product with id " + id);
        delete(endpoint, HttpStatus.OK.value());

        Allure.step("read deleted product with id " + id);
        getWithCode(endpoint, Errors.E404.getCode());

        Allure.step("repeat delete created product with id " + id);
        delete(endpoint, Errors.E404);
    }
}
