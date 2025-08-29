package com.example.demo.repos;

import com.example.demo.entity.AbstractEntity;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import java.util.UUID;

@NoRepositoryBean
public interface AbstractRepo<E extends AbstractEntity> extends CrudRepository<E, UUID>,
        PagingAndSortingRepository<E, UUID>, QueryByExampleExecutor<E> {
    Class<E> getClazz();

    default Page<E> findAllByExample(Example<E> example, Pageable pageable) {
        return findAll(example, pageable);
    }
}
