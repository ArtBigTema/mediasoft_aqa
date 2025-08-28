package com.example.demo.service;

import com.example.demo.entity.AbstractEntity;
import com.example.demo.repos.AbstractRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CrudService {
    private final Map<? extends Class<? extends AbstractEntity>, AbstractRepo<? extends AbstractEntity>> repositories;

    public CrudService(List<AbstractRepo<? extends AbstractEntity>> repositories) {
        this.repositories = repositories.stream()
                .collect(Collectors.toMap(AbstractRepo::getClazz, Function.identity()));
    }

    public <E extends AbstractEntity> E find(Class<E> clazz, UUID id) {
        return null;
    }

    public <E extends AbstractEntity> E update(UUID id, E body) {
        return body;
    }

    public <E extends AbstractEntity> void delete(UUID id, Class<E> clazz) {
    }

    public <E extends AbstractEntity> E save(E body) {
        return body;
    }
}
