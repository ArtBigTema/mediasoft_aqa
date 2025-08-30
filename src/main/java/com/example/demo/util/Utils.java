package com.example.demo.util;

import com.example.demo.entity.AbstractEntity;
import com.example.demo.rest.common.Errors;
import com.google.common.base.CaseFormat;
import com.google.common.collect.Sets;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Collections.emptySet;

@UtilityClass
public class Utils {
    public static final String PAGE_PARAMETER = "page";
    public static final String SIZE_PARAMETER = "size";
    public static final String SORT_PARAMETER = "sort";
    public static final Map<String , Predicate<Object>> pageableChecker = Map.of(
            PAGE_PARAMETER, m-> NumberUtils.createInteger(m.toString())>=NumberUtils.INTEGER_ZERO,
            SIZE_PARAMETER, m-> NumberUtils.createInteger(m.toString())>=NumberUtils.INTEGER_ONE,
            SORT_PARAMETER, Objects::nonNull
    );

    public static <E extends AbstractEntity> void validateParams(Class<E> clazz, Map<String, Object> params, Pageable pageable) {
        Set<String> fields = Arrays.stream(FieldUtils.getAllFields(clazz))
                .map(Field::getName).collect(Collectors.toSet());

        for (String key : params.keySet()) {
            Predicate<Object> checker = pageableChecker.getOrDefault(key, p -> fields.contains(key));
            Errors.E700.thr(checker.test(params.get(key))); // todo more information?
        }
        for (Sort.Order order : pageable.getSort()) {
            Errors.E700.thr(fields.contains(order.getProperty()));
        }
        params.keySet().removeIf(pageableChecker.keySet()::contains);
    }

    @FunctionalInterface
    public interface Supplier<T, E extends Throwable> {
        T get() throws E;
    }

    @FunctionalInterface
    public interface Runnable<E extends Throwable> {
        void run() throws E;
    }

    public static <T> T safetyTake(Supplier<T, Exception> getter) {
        try {
            return getter.get();
        } catch (Throwable e) {
            return null;
        }
    }

    public static String underscore2camel(String text) {
        return Objects.isNull(text) ? null : CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, text);
    }

    public static String camel2underscore(String text) {
        return Objects.isNull(text) ? null : CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, text);
    }

    public static <T> T copyNn(Object src, T target, String... ignoredFields) {
        copyProperties(src, target, emptySet(), ignoredFields);
        return target;
    }

    public static void copyNonNullProperties(Object src, Object target, String... ignoredFields) {
        copyProperties(src, target, emptySet(), ignoredFields);
    }

    public static void copyProperties(Object src, Object target, Set<String> mustCopyFields, String... ignoredFields) {
        if (Objects.isNull(src)) return;
        BeanWrapper wrappedSrc = new BeanWrapperImpl(src);
        PropertyDescriptor[] propertyDescriptors = wrappedSrc.getPropertyDescriptors();
        Set<String> emptyNames = Sets.newHashSet(ignoredFields);
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            Object srcValue = null;
            if (emptyNames.contains(propertyDescriptor.getName())) continue;
            try {
                srcValue = wrappedSrc.getPropertyValue(propertyDescriptor.getName());
            } catch (Exception ignored) {
            }
            if (srcValue == null) emptyNames.add(propertyDescriptor.getName());
        }
        emptyNames.removeAll(mustCopyFields);
        String[] result = new String[emptyNames.size()];
        BeanUtils.copyProperties(src, target, emptyNames.toArray(result));
    }
}
