package ru.ssau.loanofferservice.jpa.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.ssau.loanofferservice.common.service.AbstractServiceDao;
import ru.ssau.loanofferservice.dto.CreditDto;
import ru.ssau.loanofferservice.jpa.entity.Credit;
import ru.ssau.loanofferservice.jpa.repository.CreditRepository;

import java.util.Optional;

@Slf4j
@Service
public class CreditDaoService extends AbstractServiceDao<Credit, CreditRepository> {

    public CreditDaoService(CreditRepository repository) {
        super(repository);
    }

    public Credit findByCreditDto(CreditDto dto) {
        log.debug("Searching Credit by dto=[limitation={}, rate={}, deadline={}]", dto.getLimitation(),
                dto.getRate(), dto.getDeadline());
        Optional<Credit> optional = repository.findOne((root, query, criteriaBuilder) -> criteriaBuilder.and(
                criteriaBuilder.equal(root.get("limitation"), dto.getLimitation()),
                criteriaBuilder.equal(root.get("rate"), dto.getRate()),
                criteriaBuilder.equal(root.get("deadline"), dto.getDeadline())
        ));
        if (!optional.isPresent()) {
            log.warn("This entity not found by dto=[limitation={}, rate={}, deadline={}]", dto.getLimitation(),
                    dto.getRate(), dto.getDeadline());
            return null;
        } else {
            log.debug("Search successful by dto=[limitation={}, rate={}, deadline={}]", dto.getLimitation(),
                    dto.getRate(), dto.getDeadline());
            return optional.get();
        }

    }
}
