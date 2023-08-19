package ru.ssau.loanofferservice.service.impl;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.ssau.loanofferservice.dto.CreditDto;
import ru.ssau.loanofferservice.dto.response.ApiResponse;
import ru.ssau.loanofferservice.dto.response.Errors;
import ru.ssau.loanofferservice.jpa.dao.CreditDaoService;
import ru.ssau.loanofferservice.jpa.dao.LoanOfferDaoService;
import ru.ssau.loanofferservice.jpa.entity.Credit;
import ru.ssau.loanofferservice.jpa.entity.LoanOffer;
import ru.ssau.loanofferservice.security.config.principal.UserDetailsImpl;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreditService implements ApiService<CreditDto> {

    private final Gson gson;
    private final ModelMapper mapper;
    private final CreditDaoService daoService;
    private final LoanOfferService loanOfferService;
    private final LoanOfferDaoService loanOfferDaoService;

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public List<CreditDto> select(UserDetailsImpl principal) {
        List<Credit> credits = (List<Credit>) daoService.findAll();

        if (credits.isEmpty()) {
            log.warn("The result is empty");
            return new ArrayList<>();
        }

        List<CreditDto> result = credits.stream()
                .map(entity -> mapper.map(entity, CreditDto.class))
                .collect(Collectors.toList());

        log.debug("Select result = {}", gson.toJson(result));
        return result;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public ApiResponse get(UUID uuid) {
        if (uuid != null) {
            Credit entity = daoService.findById(uuid);
            if (entity == null) {
                log.warn("No data found");
                return errorResponse(Errors.CREDIT_NOT_FOUND.name());
            }
            return singleResponse(mapper.map(entity, CreditDto.class));
        }
        return errorResponse(Errors.PARAM_IS_EMPTY.name());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public ApiResponse create(CreditDto dto, UserDetailsImpl principal) {
        if (dto == null) {
            log.warn("param is empty");
            return null;
        }
        Credit credit = mapper.map(dto, Credit.class);
        credit = daoService.save(credit);
        log.debug("Saving completed {}", gson.toJson(credit));

        return singleResponse(mapper.map(credit, CreditDto.class));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public ApiResponse update(UUID id, CreditDto dto, UserDetailsImpl principal) {
        if (dto == null) {
            log.warn("param is empty");
            return errorResponse(Errors.PARAM_IS_EMPTY.name());
        }

        Credit credit = daoService.findById(id);
        if (credit == null) {
            log.warn("Entity not found");
            return errorResponse(Errors.CREDIT_NOT_FOUND.name());
        }
        if (dto.getDeadline() != null) {
            credit.setDeadline(dto.getDeadline());
        }
        if (dto.getRate() != null) {
            credit.setRate(dto.getRate());
        }
        if (dto.getLimitation() != null) {
            credit.setLimitation(dto.getLimitation());
        }
        credit.setUpdatedAt(ZonedDateTime.now(Clock.systemUTC()).toInstant().toEpochMilli());
        credit = daoService.save(credit);
        log.debug("Saving completed {}", gson.toJson(credit));

        return singleResponse(mapper.map(credit, CreditDto.class));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean delete(UUID uuid, UUID userId) {
        Credit entity = daoService.findById(uuid);
        if (entity == null) {
            log.warn("Entity with id={} not found", uuid.toString());
        } else {
            List<LoanOffer> loanOfferList = loanOfferDaoService.findAllByCredit(entity);
            if (!loanOfferList.isEmpty()) {
                loanOfferList.forEach(loanOffer -> loanOfferService.delete(loanOffer.getId(), userId));
            }

            entity.setDeletedBy(userId);
            daoService.save(entity);

            log.debug("Deleted is completed by id {}, deleted by user {}", uuid, userId);
            return true;
        }
        return false;
    }
}
