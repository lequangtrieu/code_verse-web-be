package codeverse.com.web_be.service;

import java.util.List;
import java.util.Optional;

public interface IGenericService<T, ID> {
    List<T> findAll();

    Optional<T> findById(ID id);

    T save(T entity);

    T update(T entity);

    void deleteById(ID id);

    boolean existsById(ID id);
}