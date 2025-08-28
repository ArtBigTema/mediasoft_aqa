package com.example.demo.rest;

import com.example.demo.util.Utils;
import com.google.common.collect.ImmutableMap;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class CommonController {
    private final JdbcTemplate jdbcTemplate;

    @SneakyThrows
    @GetMapping("jdbc")
    public Object table(@RequestParam String table) {
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(String.format("select * from INFORMATION_SCHEMA.COLUMNS" +
                " where table_name = '%s'", table));
        StringBuilder sb = new StringBuilder();
        List<Triple<Object, Object, Object>> collect = maps.stream().map(s -> Triple.of(s.get("column_name"), s.get("data_type"), s.get("is_nullable")))
                .toList();
        sb.append("import jakarta.validation.constraints.NotNull; " + "import lombok.Data; " +
                        "import lombok.EqualsAndHashCode; " + " import jakarta.persistence.Entity; import ru.emias.mcc.domain.entity.core.IdentityEntity; " +
                        " " + "import java.time.LocalDateTime; " +
                        " @Data @Entity @EqualsAndHashCode(callSuper = true) ").append("public class ").append(StringUtils.capitalize(Utils.underscore2camel(table)))
                .append(" extends AbstractEntity { ").append("   ");
        for (Triple<Object, Object, Object> p : collect) {
            if (p.getLeft().toString().equals("id")) continue;
            sb.append("      ");
            String s = p.getMiddle().toString();
            if (!BooleanUtils.toBoolean(p.getRight().toString())) {
                sb.append("@NotNull");
                sb.append("   ");
            }
            sb.append("private ");
            if (s.contains("char") || s.contains("text")) {
                sb.append("String");
            }
            if (s.contains("bool")) {
                sb.append("Boolean");
            } else if (s.contains("boolean")) {
                sb.append("Boolean");
            }
            if (s.contains("integer")) {
                sb.append("Integer");
            }
            if (s.contains("bigint")) {
                sb.append("Long");
            }
            if (s.contains("numb")) {
                sb.append("Long");
            }
            if (s.contains("numeric")) {
                sb.append("Double");
            }
            if (s.contains("date")) {
                sb.append("LocalDate");
            }
            if (s.contains("timestamp")) {
                sb.append("LocalDateTime");
            }
            sb.append(" ");
            sb.append(Utils.underscore2camel(p.getLeft().toString()));
            sb.append(";");
        }
        sb.append(" }");
        return ImmutableMap.of("class", sb.toString());
    }
}
