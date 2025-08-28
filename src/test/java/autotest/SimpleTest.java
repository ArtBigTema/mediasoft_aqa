package autotest;

import org.junit.jupiter.api.Test;

public class SimpleTest extends AuthTestCase {
    @Test
    public void test() {
        get("http://localhost:8080/api/products/a03a6994-e9fa-489f-a33c-0645b1f875ea");
    }
}
