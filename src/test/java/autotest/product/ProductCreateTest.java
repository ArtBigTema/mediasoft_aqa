package autotest.product;

import autotest.AuthTestCase;
import autotest.util.Constant;
import autotest.util.Random;
import com.example.demo.entity.Product;
import io.qameta.allure.Step;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;

import static autotest.util.Constant.AUTO_TEST_CATEGORY;
import static autotest.util.Endpoints.PRODUCT_ENDPOINT;

@Tags({@Tag("Product"), @Tag("post")})
public class ProductCreateTest extends AuthTestCase {

    public static Object createProduct() {
        return createAndSaveProductWithCategory(AUTO_TEST_CATEGORY);
    }

    @Step("save created random product")
    public static Object createAndSaveProductWithCategory(String category) {
        return post(PRODUCT_ENDPOINT, createProductWithCategory(category))
                .extract().body().path(Constant.ID_DATA_FIELD);
    }

    @Step("create random product")
    public static Product createProductWithCategory(String category) {
        return Random.getInstance().nextObject(Product.class).setCategory(category);
    }
}
