package autotest.util;

import com.example.demo.util.Utils;
import io.restassured.internal.util.IOUtils;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@UtilityClass
@SuppressWarnings("all")
public class Constant {
    public static final String AUTO_TEST_CATEGORY = "autoTestCategory";

    public static final String DESCRIPTION_FIELD = "description";
    public static final String RESULT_FIELD = "result";
    public static final String MESSAGE_FIELD = "message";
    public static final String DATA_FIELD = "data";
    public static final String CODE_FIELD = "code";
    public static final String MAP_FIELD = "map";
    public static final Long MAX_TIMEOUT = 2_000L;
    public static final Class<Comparable> COMPARABLE_CLASS = Comparable.class;

    public static final String BASE_URL;
    public static final Map<String, String> env;

    public static Comparator<Map> mapComparator(String field) {
        return Comparator.comparing(m -> {
            Object o = m.get(field);
            if (o instanceof String str) {
                return COMPARABLE_CLASS.cast(str.toLowerCase());
            }
            return COMPARABLE_CLASS.cast(o);
        });
    }

    static { // вычитка конфигов из app.prop локально и из энвов на стэндах
        String props = Utils.safetyTake(() -> new String(IOUtils.toByteArray(
                Objects.requireNonNull(Constant.class.getClassLoader()
                        .getResourceAsStream("application.properties")))));
        String[] split = StringUtils.split(props, '\n');
        env = Arrays.stream(Objects.requireNonNull(split))
                .filter(s -> !s.startsWith("#")).filter(StringUtils::isNotBlank)
                .collect(Collectors.toMap(s -> StringUtils.substringBefore(s, "="),
                        s -> StringUtils.substringBetween(s, ":", "}")));

        BASE_URL = getFromEnvOrProps("service.endpoint");
    }

    private static String getFromEnvOrProps(String path) {
        return Optional
                .ofNullable(System.getProperty(path))
                .or(() -> Optional.ofNullable(System.getenv(path)))
                .orElseGet(() -> env.get(StringUtils.replaceChars(
                        path.toLowerCase(), '_', '.')));
    }
}
