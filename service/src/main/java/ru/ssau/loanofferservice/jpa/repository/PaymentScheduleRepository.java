package ru.ssau.loanofferservice.jpa.repository;

import org.springframework.stereotype.Repository;
import ru.ssau.loanofferservice.common.dao.repository.AbstractRepository;
import ru.ssau.loanofferservice.jpa.entity.LoanOffer;
import ru.ssau.loanofferservice.jpa.entity.PaymentSchedule;

import java.util.List;

@Repository
public interface PaymentScheduleRepository extends AbstractRepository<PaymentSchedule> {

    List<PaymentSchedule> findAllByLoanOffer(LoanOffer loanOffer);
}
