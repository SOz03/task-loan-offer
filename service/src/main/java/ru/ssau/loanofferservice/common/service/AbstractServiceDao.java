package ru.ssau.loanofferservice.common.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.ssau.loanofferservice.common.dao.entity.BaseEntity;
import ru.ssau.loanofferservice.common.dao.repository.AbstractRepository;

import java.util.Optional;
import java.util.UUID;

import static org.springframework.transaction.annotation.Propagation.REQUIRED;

@Slf4j
public abstract class AbstractServiceDao<E extends BaseEntity, R extends AbstractRepository<E>> {

    protected final R repository;

    @Autowired
    public AbstractServiceDao(R repository) {
        this.repository = repository;
    }

    @Transactional(propagation = REQUIRED, readOnly = true)
    public Iterable<E> findAll() {
        return repository.findAll();
    }

    @Transactional(propagation = REQUIRED, readOnly = true)
    public E findById(UUID id) {
        Optional<E> op = repository.findById(id);

        if (!op.isPresent()) {
            log.error("Row with id: {} is not found in database", id);
            return null;
        }

        return op.get();
    }

    @Transactional(propagation = REQUIRED)
    public E save(E entity) {
        return repository.save(entity);
    }

    @Transactional(propagation = REQUIRED)
    public Iterable<E> saveAll(Iterable<E> entities) {
        return repository.saveAll(entities);
    }

}
