package com.example.demo.util;

import com.google.common.base.CaseFormat;
import com.google.common.collect.Sets;
import lombok.experimental.UtilityClass;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.util.Objects;
import java.util.Set;

import static java.util.Collections.emptySet;

@UtilityClass
public class Utils {
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
