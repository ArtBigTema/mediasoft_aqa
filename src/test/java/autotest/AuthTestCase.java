package autotest;

import autotest.util.Constant;
import com.example.demo.config.AnyConfig;
import com.example.demo.rest.common.Errors;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.http.Method;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.actuate.health.Status;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;

import static autotest.util.Constant.BASE_URL;
import static io.restassured.RestAssured.given;
import static org.apache.commons.lang3.math.NumberUtils.INTEGER_ONE;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.COOKIE;


public abstract class AuthTestCase {
    public static String cookie = "";
    public static final ObjectMapper mapper = AnyConfig.objectMapper();


    @BeforeAll
    public static void auth() {
        get(BASE_URL + "actuator/health")
                .body("status", Matchers.equalTo(Status.UP.getCode()));
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
        RestAssured.config = RestAssuredConfig.config().objectMapperConfig(
                new ObjectMapperConfig().jackson2ObjectMapperFactory((cls, charset) -> mapper));
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

    public static ValidatableResponse getWithError(String url, Map<String, Object> params, Errors errors) {
        return execute(url, errors.getCode(), Method.GET, params, Function.identity())
                .body(Constant.RESULT_FIELD, equalTo(Boolean.FALSE))
                .body(Constant.CODE_FIELD, equalTo(errors.name().substring(INTEGER_ONE)))
                .body(Constant.MESSAGE_FIELD, Matchers.anyOf(
                        Matchers.matchesRegex(errors.toRegexp()),
                        containsString(errors.getDescription()))
                );
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
     * Выполняет DELETE запрос к заданному URL с определенным кодом ответа
     *
     * @param url  целевой адрес
     * @param code http код ответа
     * @return провалидированный по коду ответ
     */
    public static ValidatableResponse delete(String url, int code) {
        return execute(url, code, Method.DELETE);
    }

    public static ValidatableResponse delete(String url, Errors errors) {
        return execute(url, errors.getCode(), Method.DELETE)
                .body(Constant.RESULT_FIELD, equalTo(Boolean.FALSE))
                .body(Constant.CODE_FIELD, equalTo(errors.name().substring(INTEGER_ONE)))
                .body(Constant.MESSAGE_FIELD, Matchers.anyOf(
                        Matchers.matchesRegex(errors.toRegexp()),
                        containsString(errors.getDescription()))
                );
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
        url = StringUtils.prependIfMissing(url, BASE_URL);
        url = StringUtils.replace(url, "/?", "?"); // todo убрать костыль удаления /?
        url = StringUtils.removeEnd(url, "/"); // todo убрать костыль окончания на слэш
        return adapter.apply(given()
                        // todo выбрать один способ авторизации
                        .header(COOKIE, StringUtils.prependIfMissing(cookie, "access_token="))
                        .header(AUTHORIZATION, StringUtils.prependIfMissing(cookie, "Bearer "))
                        .when())
                .queryParams(params)
                .request(method, url)
                .then()
                .time(Matchers.lessThan(Constant.MAX_TIMEOUT))
                .statusCode(code);
    }
}
