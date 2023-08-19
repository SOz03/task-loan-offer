package ru.ssau.loanofferservice.jpa.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.ssau.loanofferservice.common.service.AbstractServiceDao;
import ru.ssau.loanofferservice.jpa.entity.LoanOffer;
import ru.ssau.loanofferservice.jpa.entity.PaymentSchedule;
import ru.ssau.loanofferservice.jpa.repository.PaymentScheduleRepository;

import java.util.List;

@Slf4j
@Service
public class PaymentScheduleDaoService extends AbstractServiceDao<PaymentSchedule, PaymentScheduleRepository> {

    public PaymentScheduleDaoService(PaymentScheduleRepository repository) {
        super(repository);
    }

    public List<PaymentSchedule> getAllByLoanOffer(LoanOffer loanOffer) {
        return repository.findAllByLoanOffer(loanOffer);
    }

}
