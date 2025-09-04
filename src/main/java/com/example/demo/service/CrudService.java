package com.example.demo.service;

import com.example.demo.entity.AbstractEntity;
import com.example.demo.entity.Customer;
import com.example.demo.repos.AbstractRepo;
import com.example.demo.rest.common.Errors;
import com.example.demo.util.Utils;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class CrudService {
    private final ObjectMapper mapper;
    private final Map<? extends Class<? extends AbstractEntity>, AbstractRepo<? extends AbstractEntity>> repositories;
    public static final ExampleMatcher MATCHER = ExampleMatcher.matching()
            .withIgnoreCase().withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);

    public CrudService(ObjectMapper mapper, List<AbstractRepo<? extends AbstractEntity>> repositories) {
        this.mapper = mapper.copy().disable(MapperFeature.USE_ANNOTATIONS);
        this.repositories = repositories.stream()
                .collect(Collectors.toMap(AbstractRepo::getClazz, Function.identity()));
    }

    public <E extends AbstractEntity> E find(Class<E> clazz, UUID id) {
        return clazz.cast(repositories.get(clazz).findById(id)
                .orElseThrow(() -> Errors.E404.thr(clazz.getSimpleName(), id)));
    }

    public <E extends AbstractEntity> Iterable<E> find(Class<E> clazz, Collection<UUID> id) {
        return getRepository(clazz).findAllById(id);
    }

    public <E extends AbstractEntity> Map<UUID, E> findAll(Class<E> clazz, Collection<UUID> id) {
        Iterable<E> i = find(clazz, id);
        Map<UUID, E> index = StreamSupport.stream(i.spliterator(), false)
                .collect(Collectors.toMap(AbstractEntity::getId, Function.identity()));
        Errors.E404.thr(index.keySet().equals(id));
        return index;
    }

    public <E extends AbstractEntity> E update(UUID id, Class<E> clazz, E body) {
        E old = find(clazz, id);
        Utils.copyNonNullProperties(body, old);
        return getRepository(clazz).save(old);
    }

    public <E extends AbstractEntity> void delete(UUID id, Class<E> clazz) {
        getRepository(clazz).deleteById(id);
    }

    public <E extends AbstractEntity> E save(Class<E> clazz, E body) {
        body.onCreate(this);
        E saved = getRepository(clazz).save(body);
        saved.getMap().clear();
        return saved;
    }

    public <E extends AbstractEntity> Page<E> findAll(Class<E> clazz, Map<String, Object> params, Pageable pageable) {
        Example<E> example = Example.of(mapper.convertValue(params, clazz), MATCHER);
        AbstractRepo<E> repository = getRepository(clazz);
        return repository.findAllByExample(example, pageable);
    }

    @SuppressWarnings("unchecked")
    private <E extends AbstractEntity> AbstractRepo<E> getRepository(Class<E> clazz) {
        return (AbstractRepo<E>) Errors.E500.thr(()-> repositories.get(clazz));
    }

    public <E extends AbstractEntity> void exist(Class<E> clazz, UUID id) {
        Errors.E404.thr(getRepository(clazz).existsById(id), clazz.getSimpleName(), id);
    }
}
