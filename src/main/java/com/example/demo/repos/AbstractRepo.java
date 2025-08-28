package com.example.demo.repos;

import com.example.demo.entity.AbstractEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.UUID;

@NoRepositoryBean
public interface AbstractRepo<E extends AbstractEntity> extends CrudRepository<E, UUID> {
    Class<E> getClazz();
}
