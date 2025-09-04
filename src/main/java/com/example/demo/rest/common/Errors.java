package com.example.demo.rest.common;


import com.example.demo.util.Utils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.Objects;

@Getter
@RequiredArgsConstructor
public enum Errors {
    E404(404, "Сущность %s с идентификатом %s не найдена"),
    E105(500, "Запись с идентификатором '%s' удалена"),
    E731(500, "Удаление запрещено"),
    E401(401, "Необходимо авторизоваться"),
    E403(403, "Доступ запрещен"),
    E700(418, "Некорректные входные параметры. "),
    E101(500, "Обязательные входные параметры %s не заполнены"),
    E113(500, "Данные не найдены. Измените условия запроса"),
    E505(500, "Невозможно создать корзину, не хватает кол-ва товара"),
    E506(500, "Невозможно создать корзину, продукт недоступен"),
    E500(500, "Непредвиденная ошибка. ");

    private final int code; // todo
    private final String description;

    public String getCustomCode() {
        return StringUtils.substring(this.name(), NumberUtils.INTEGER_ONE);
    }

    public String toRegexp() {
        return "^" + StringUtils.replace(getDescription(), "%s", "\\S+") + "$";
    }

    public CodifiedException thr(Object... args) {
        return new CodifiedException(this, String.format(this.description, args));
    }

    public <E> E thr(Utils.Supplier<E, Throwable> supplier) {
        try {
            return Objects.requireNonNull(supplier.get());
        } catch (CodifiedException e) {
            throw e;
        } catch (Throwable t) {
            throw new CodifiedException(this, t);
        }
    }

    public <E> void thr(String msg, Utils.Runnable<Throwable> supplier) {
        try {
            supplier.run();
        } catch (Throwable e) {
            throw new CodifiedException(this, e, msg);
        }
    }

    public void thrIf(Boolean isTrue, Object... args) {
        thr(BooleanUtils.isNotTrue(isTrue), args);
    }

    public void thr(Boolean isTrue, Object... args) {
        if (Boolean.TRUE.equals(isTrue)) return;
        String dsc = this.description;
        if (ArrayUtils.isNotEmpty(args)) {
            if (StringUtils.contains(dsc, "%s")) {
                dsc = String.format(dsc, args);
            } else {
                dsc += StringUtils.joinWith(", ", args);
            }
        }
        throw new CodifiedException(this, dsc);
    }

    @Getter
    @AllArgsConstructor
    @RequiredArgsConstructor
    public static class CodifiedException extends RuntimeException {
        private final Errors error;
        private String msg;

        public CodifiedException(Errors error, Throwable t) {
            this(error, t, error.getDescription());
        }

        public CodifiedException(Errors error, Throwable t, String msg) {
            super(error.getDescription(), t);
            this.error = error;
            this.msg = msg; //+ ". " + t.getMessage();
        }

        @Override
        public String getMessage() {
            return StringUtils.defaultIfEmpty(getMsg(), super.getMessage());
        }
    }
}
