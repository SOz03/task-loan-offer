package ru.ssau.loanofferservice.jpa.repository;

import org.springframework.stereotype.Repository;
import ru.ssau.loanofferservice.common.dao.repository.AbstractRepository;
import ru.ssau.loanofferservice.jpa.entity.Bank;

import java.util.List;

@Repository
public interface BankRepository extends AbstractRepository<Bank> {

    List<Bank> findAllByBankNameContaining(String name);

}
