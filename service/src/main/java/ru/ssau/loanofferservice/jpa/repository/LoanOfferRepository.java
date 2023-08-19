package ru.ssau.loanofferservice.jpa.repository;

import org.springframework.stereotype.Repository;
import ru.ssau.loanofferservice.common.dao.repository.AbstractRepository;
import ru.ssau.loanofferservice.jpa.entity.Bank;
import ru.ssau.loanofferservice.jpa.entity.Credit;
import ru.ssau.loanofferservice.jpa.entity.LoanOffer;
import ru.ssau.loanofferservice.jpa.entity.User;

import java.util.List;

@Repository
public interface LoanOfferRepository extends AbstractRepository<LoanOffer> {

    List<LoanOffer> findAllByBankAndUser(Bank bank, User user);

    LoanOffer findLoanOfferByBankAndUserAndCredit(Bank bank, User user, Credit credit);

    List<LoanOffer> findAllByBank(Bank bank);

    List<LoanOffer> findAllByCredit(Credit credit);

}
