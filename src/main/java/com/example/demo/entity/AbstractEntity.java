package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;

@Data
@MappedSuperclass
@FieldNameConstants
public abstract class AbstractEntity implements Serializable {
    @Id
    @Getter
    @Setter
    @JsonProperty(access = READ_ONLY)
    UUID id;

    @Transient
    @JsonIgnore
    protected Map<String, Object> map = new HashMap<>();

    @PrePersist
    private void pre() {
        id = UUID.randomUUID(); // todo generate by seq
    }

    /**
     * Сеттер для мапы с новыми значениями.
     *
     * @param propertyKey ключ
     * @param value       значение
     */
    @JsonAnySetter
    public void setMap(String propertyKey, Object value) {
        this.map.put(propertyKey, value);
    }

    @JsonIgnore
    public void setObject(Object object) {
        setMap(StringUtils.uncapitalize(object.getClass().getSimpleName()), object);
    }

    /**
     * Геттер для мапы с новыми значениями.
     *
     * @return мапа с новыми значениями
     */
    @JsonAnyGetter
    public Map<String, Object> getMap() {
        return map;
    }
}
