package com.example.demo.service;

import com.example.demo.entity.AbstractEntity;
import com.example.demo.repos.AbstractRepo;
import com.example.demo.rest.common.Errors;
import com.example.demo.util.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CrudService {
    private final ObjectMapper mapper;
    private final Map<? extends Class<? extends AbstractEntity>, AbstractRepo<? extends AbstractEntity>> repositories;
    public static final ExampleMatcher MATCHER = ExampleMatcher.matching()
            .withIgnoreCase().withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);

    public CrudService(ObjectMapper mapper, List<AbstractRepo<? extends AbstractEntity>> repositories) {
        this.mapper = mapper;
        this.repositories = repositories.stream()
                .collect(Collectors.toMap(AbstractRepo::getClazz, Function.identity()));
    }

    public <E extends AbstractEntity> E find(Class<E> clazz, UUID id) {
        return clazz.cast(repositories.get(clazz).findById(id)
                .orElseThrow(() -> Errors.E103.thr(clazz.getSimpleName(), id)));
    }

    public <E extends AbstractEntity> E update(UUID id, Class<E> clazz, E body) {
        E old = find(clazz, id);
        Utils.copyNonNullProperties(body, old);
        return getRepository(clazz).save(old);
    }

    public <E extends AbstractEntity> void delete(UUID id, Class<E> clazz) {
        AbstractRepo<E> repository = getRepository(clazz);
        Errors.E113.thr(repository.existsById(id));
        repository.deleteById(id);
    }

    public <E extends AbstractEntity> E save(Class<E> clazz, E body) {
        return getRepository(clazz).save(body);
    }

    public <E extends AbstractEntity> Page<E> findAll(Class<E> clazz, Map<String, Object> params, Pageable pageable) {
        Example<E> example = Example.of(mapper.convertValue(params, clazz), MATCHER);
        AbstractRepo<E> repository = getRepository(clazz);
        return repository.findAllByExample(example, pageable);
    }

    @SuppressWarnings("unchecked")
    private <E extends AbstractEntity> AbstractRepo<E> getRepository(Class<E> clazz) {
        return (AbstractRepo<E>) repositories.get(clazz);
    }
}
