package com.example.demo.rest.common;


import com.example.demo.util.Utils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

@Getter
@RequiredArgsConstructor
public enum Errors {
    E110(500, "Статус публикации не соответствует матрице переходов статусов публикации"),
    E103(500, "Сущность %s с идентификатом %s не найдена"),
    E105(500, "Запись с идентификатором '%s' удалена"),
    E731(500, "Удаление запрещено"),
    E401(401, "Необходимо авторизоваться"),
    E403(403, "Доступ запрещен"),
    E108(500, "Пользователь с такими данными не найден"),
    E700(500, "Некорректные входные параметры: %s  должен быть задан"),
    E109(500, "Сущность находится в обновляемой публикации"),
    E101(500, "Обязательные входные параметры %s не заполнены"),
    E112(500, "Невозможно создать пресет. Максимальное количество пресетов создано"),
    E113(500, "Данные не найдены. Измените условия запроса"),
    E500(500, "Непредвиденная ошибка. ");

    private final int code; // todo
    private final String description;

    public String getCustomCode() {
        return StringUtils.substring(this.name(), NumberUtils.INTEGER_ONE);
    }

    public CodifiedException thr(Object... args) {
        return new CodifiedException(this, String.format(this.description, args));
    }

    public <E> E thr(Utils.Supplier<E, Throwable> supplier) {
        try {
            return supplier.get();
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
            return StringUtils.defaultString(getMsg(), super.getMessage());
        }
    }
}
