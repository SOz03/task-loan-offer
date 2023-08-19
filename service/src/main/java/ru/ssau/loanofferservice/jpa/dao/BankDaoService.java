package ru.ssau.loanofferservice.jpa.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.ssau.loanofferservice.common.service.AbstractServiceDao;
import ru.ssau.loanofferservice.jpa.entity.Bank;
import ru.ssau.loanofferservice.jpa.repository.BankRepository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class BankDaoService extends AbstractServiceDao<Bank, BankRepository> {

    public BankDaoService(BankRepository repository) {
        super(repository);
    }

    public List<Bank> getAllByName(String name) {
        log.debug("Searching bank for by name={}", name);

        List<Bank> list = repository.findAllByBankNameContaining(name);
        if (list.isEmpty()) {
            return new ArrayList<>();
        } else {
            log.debug("Search successful banks is size={}", list.size());
            return list;
        }
    }

}
