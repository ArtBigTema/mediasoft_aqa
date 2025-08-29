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
        return createProductWithCategory(AUTO_TEST_CATEGORY);
    }

    @Step("create random product")
    public static Object createProductWithCategory(String category) {
        Product product = Random.getInstance().nextObject(Product.class);
        return post(PRODUCT_ENDPOINT, product.setCategory(category))
                .extract().body().path(Constant.DATA_FIELD);
    }
}
