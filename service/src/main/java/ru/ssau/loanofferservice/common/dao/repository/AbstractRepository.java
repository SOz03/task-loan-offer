package ru.ssau.loanofferservice.common.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;
import ru.ssau.loanofferservice.common.dao.entity.BaseEntity;

import java.util.UUID;

@NoRepositoryBean
public interface AbstractRepository<E extends BaseEntity> extends JpaSpecificationExecutor<E>, JpaRepository<E,UUID> {

}

