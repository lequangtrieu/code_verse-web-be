package codeverse.com.web_be.service;

import codeverse.com.web_be.exception.AppException;
import codeverse.com.web_be.exception.ErrorCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public abstract class GenericServiceImpl<T, ID> implements IGenericService<T, ID> {

    protected final JpaRepository<T, ID> repository;

    protected GenericServiceImpl(JpaRepository<T, ID> repository) {
        this.repository = repository;
    }

    @Override
    public List<T> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<T> findById(ID id) {
        return repository.findById(id);
    }

    @Override
    public T save(T t) {
        return repository.save(t);
    }

    @Override
    public T update(T t) {
        return repository.save(t);
    }

    @Override
    public void deleteById(ID id) {
        if(!existsById(id)) throw new AppException(ErrorCode.NOT_EXISTED_ENTITY);
        repository.deleteById(id);
    }

    @Override
    public boolean existsById(ID id) {
        return repository.existsById(id);
    }
}