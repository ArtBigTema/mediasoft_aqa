package autotest;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.http.Method;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.auth.AUTH;
import org.apache.http.cookie.SM;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;

import static io.restassured.RestAssured.given;


public abstract class AuthTestCase {
    public static String cookie = "";

    @BeforeAll
    public static void auth() {
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());

        get("http://localhost:8080/api/actuator/health")
                .body("status", Matchers.equalTo("UP"));
    }

    /**
     * Возвращает ответ метода GET по заданному URL
     *
     * @param url целевой адрес
     * @return ответ запроса
     */
    public static ValidatableResponse get(String url) {
        return getWithCode(url, 200);
    }

    /**
     * Выполняет GET запрос к заданному URL с 200 кодом ответа и параметрами запроса
     *
     * @param url    целевой адрес
     * @param params http query parameters
     * @return провалидированный по 200 коду ответ
     */
    public static ValidatableResponse getWithParams(String url, Map<String, Object> params) {
        return execute(url, 200, Method.GET, params, Function.identity());
    }

    /**
     * Выполняет GET запрос к заданному URL с определенным кодом ответа и телом запроса
     *
     * @param url  целевой адрес
     * @param code http код ответа
     * @return провалидированный по коду ответ
     */
    public static ValidatableResponse getWithCode(String url, int code) {
        return execute(url, code, Method.GET);
    }

    /**
     * Выполняет POST запрос к заданному URL с 201 кодом ответа и телом запроса
     *
     * @param url  целевой адрес
     * @param body тело запроса
     * @return провалидированный по 201 коду ответ
     */
    public static ValidatableResponse post(String url, Object body) {
        return execute(url, 201, Method.POST, body);
    }

    /**
     * Выполняет POST запрос к заданному URL с определенным кодом ответа и телом запроса
     *
     * @param url  целевой адрес
     * @param code http код ответа
     * @param body тело запроса
     * @return провалидированный по коду ответ
     */
    public static ValidatableResponse post(String url, int code, Object body) {
        return execute(url, code, Method.POST, body);
    }

    /**
     * Выполняет PUT запрос к заданному URL с определенным кодом ответа и телом запроса
     *
     * @param url  целевой адрес
     * @param code http код ответа
     * @param body тело запроса
     * @return провалидированный по коду ответ
     */
    public static ValidatableResponse put(String url, int code, Object body) {
        return execute(url, code, Method.PUT, body);
    }

    /**
     * Выполняет запрос к заданному URL с определенным кодом ответа, типом метода
     *
     * @param url    целевой адрес
     * @param code   http код ответа
     * @param method http метод: POST, PUT, GET
     * @return провалидированный по коду ответ
     */
    public static ValidatableResponse execute(String url, int code, Method method) {
        return execute(url, code, method, Collections.emptyMap(), Function.identity());
    }

    /**
     * Выполняет запрос к заданному URL с определенным кодом ответа, типом метода, боди если это POST или PUT
     *
     * @param url    целевой адрес
     * @param code   http код ответа
     * @param method http метод: POST, PUT, GET
     * @param body   тело запроса, обычно json или dto
     * @return провалидированный по коду ответ
     */
    public static ValidatableResponse execute(String url, int code, Method method, Object body) {
        return execute(url, code, method, Collections.emptyMap(),
                f -> f.contentType(ContentType.JSON).body(body));
    }

    /**
     * Выполняет запрос к заданному URL с определенным кодом ответа, типом метода
     *
     * @param url    целевой адрес
     * @param code   http код ответа
     * @param method http метод: POST, PUT, GET
     * @param type   тип тела запроса, обычно json
     * @return провалидированный по коду ответ
     */
    public static ValidatableResponse execute(String url, int code, Method method, ContentType type) {
        return execute(url, code, method, Collections.emptyMap(),
                f -> f.contentType(type));
    }

    /**
     * Выполняет запрос к заданному URL с определенным кодом ответа, типом метода, набором параметров, боди если это POST или PUT
     *
     * @param url     целевой адрес
     * @param code    http код ответа
     * @param method  http метод: POST, PUT, GET
     * @param params  query parameters
     * @param adapter кастомизатор body (добавляет contentType)
     * @return провалидированный по коду ответ
     */
    public static ValidatableResponse execute(String url, int code, Method method, Map<String, Object> params,
                                              Function<RequestSpecification, RequestSpecification> adapter) {
        url = StringUtils.replace(url, "/?", "?"); // todo убрать костыль удаления /?, т.к. падает в 401
        url = StringUtils.removeEnd(url, "/"); // todo убрать костыль окончания на слэш, т.к. падает в 401
        return adapter.apply(given()
                        // todo выбрать один способ авторизации
                        .header(SM.COOKIE, StringUtils.prependIfMissing(cookie, "access_token="))
                        .header(AUTH.WWW_AUTH_RESP, StringUtils.prependIfMissing(cookie, "Bearer "))
                        .when())
                .queryParams(params)
                .request(method, url)
                .then()
                .statusCode(code);
    }
}
