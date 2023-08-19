package ru.ssau.loanofferservice.jpa.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.ssau.loanofferservice.common.service.AbstractServiceDao;
import ru.ssau.loanofferservice.dto.LoanOfferDto;
import ru.ssau.loanofferservice.jpa.entity.Bank;
import ru.ssau.loanofferservice.jpa.entity.Credit;
import ru.ssau.loanofferservice.jpa.entity.LoanOffer;
import ru.ssau.loanofferservice.jpa.entity.User;
import ru.ssau.loanofferservice.jpa.repository.LoanOfferRepository;
import ru.ssau.loanofferservice.security.config.principal.UserDetailsImpl;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class LoanOfferDaoService extends AbstractServiceDao<LoanOffer, LoanOfferRepository> {

    public LoanOfferDaoService(LoanOfferRepository repository) {
        super(repository);
    }

    public List<LoanOffer> findAllByBank(Bank bank){
        return repository.findAllByBank(bank);
    }

    public List<LoanOffer> findAllByCredit(Credit credit){
        return repository.findAllByCredit(credit);
    }

    public List<LoanOffer> findAllByBankAndUser(Bank bank, User user) {
        return repository.findAllByBankAndUser(bank, user);
    }

    public LoanOffer getLoanOffer(Bank bank, User user, Credit credit) {
        return repository.findLoanOfferByBankAndUserAndCredit(bank, user, credit);
    }

    public LoanOffer findByLoanOfferDto(LoanOfferDto dto, UserDetailsImpl principal) {
        log.debug("Searching LoanOffer by dto=[bank={}, limit={}]", dto.getBank().getBankName(),
                dto.getCredit().getLimitation().toString());
        Optional<LoanOffer> optional = repository.findOne((root, query, criteriaBuilder) -> {
            Join<LoanOffer, Bank> bankJoin = root.join("bank", JoinType.LEFT);
            Join<LoanOffer, User> userJoin = root.join("user", JoinType.LEFT);
            Join<LoanOffer, Credit> creditJoin = root.join("credit", JoinType.LEFT);
            return criteriaBuilder.and(
                    criteriaBuilder.equal(root.get("loanAmount"), dto.getLoanAmount()),
                    criteriaBuilder.equal(bankJoin.get("bankName"), dto.getBank().getBankName()),
                    criteriaBuilder.equal(userJoin.get("username"), principal.getUsername()),
                    criteriaBuilder.and(
                            criteriaBuilder.equal(creditJoin.get("limitation"), dto.getCredit().getLimitation()),
                            criteriaBuilder.equal(creditJoin.get("rate"), dto.getCredit().getRate()),
                            criteriaBuilder.equal(creditJoin.get("deadline"), dto.getCredit().getDeadline())
                    )
            );
        });
        if (!optional.isPresent()) {
            log.debug("This entity not found by dto=[bank={}, limit={}]", dto.getBank().getBankName(),
                    dto.getCredit().getLimitation().toString());
            return null;
        } else {
            log.debug("Search successful by dto=[bank={}, limit={}]", dto.getBank().getBankName(),
                    dto.getCredit().getLimitation().toString());
            return optional.get();
        }
    }

    public List<LoanOffer> findAllBySpec(UserDetailsImpl principal) {
        log.debug("Searching entity by principal, where username = {}", principal.getUsername());
        List<LoanOffer> result = repository.findAll((root, query, criteriaBuilder) -> {
            Join<LoanOffer, User> userJoin = root.join("user", JoinType.LEFT);
            return criteriaBuilder.equal(userJoin.get("username"), principal.getUsername());
        });
        if (result.isEmpty()) {
            log.debug("This entities not found by principal, where username = {}", principal.getUsername());
            return new ArrayList<>();
        } else {
            log.debug("Search successful by principal, where username = {}", principal.getUsername());
            return result;
        }
    }
}
