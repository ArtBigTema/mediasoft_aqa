package com.example.demo.util;

import com.google.common.base.CaseFormat;

import java.util.Objects;

public class Utils {
    @FunctionalInterface
    public interface Supplier<T, E extends Throwable> {
        T get() throws E;
    }

    @FunctionalInterface
    public interface Runnable<E extends Throwable> {
        void run() throws E;
    }

    public static String underscore2camel(String text) {
        return Objects.isNull(text) ? null : CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, text);
    }

    public static String camel2underscore(String text) {
        return Objects.isNull(text) ? null : CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, text);
    }
}
