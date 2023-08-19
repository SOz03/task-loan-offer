package ru.ssau.loanofferservice.service.impl;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.ssau.loanofferservice.dto.PaymentScheduleDto;
import ru.ssau.loanofferservice.dto.enums.Role;
import ru.ssau.loanofferservice.dto.response.ApiResponse;
import ru.ssau.loanofferservice.dto.response.Errors;
import ru.ssau.loanofferservice.jpa.dao.LoanOfferDaoService;
import ru.ssau.loanofferservice.jpa.dao.PaymentScheduleDaoService;
import ru.ssau.loanofferservice.jpa.entity.LoanOffer;
import ru.ssau.loanofferservice.jpa.entity.PaymentSchedule;
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
public class PaymentScheduleService implements ApiService<PaymentScheduleDto> {

    private final Gson gson;
    private final ModelMapper mapper;
    private final PaymentScheduleDaoService daoService;
    private final LoanOfferDaoService loanOfferDaoService;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public List<PaymentScheduleDto> select(UserDetailsImpl principal) {
        boolean isUser = principal.getAuthorities().stream()
                .filter(role -> role.getAuthority().equals(Role.USER.name()))
                .count() == 1;

        log.debug("Search for all entities");
        List<LoanOffer> loanOfferList;
        if (isUser) {
            loanOfferList = loanOfferDaoService.findAllBySpec(principal);
        } else {
            loanOfferList = (List<LoanOffer>) loanOfferDaoService.findAll();
        }

        if (loanOfferList.isEmpty()) {
            log.debug("The result is empty");
            return new ArrayList<>();
        }

        List<PaymentScheduleDto> content = new ArrayList<>();
        for (LoanOffer loanOffer : loanOfferList) {
            if (loanOffer.getPaymentSchedules() != null) {
                content.addAll(
                        loanOffer.getPaymentSchedules().stream()
                                .map(entity -> mapper.map(entity, PaymentScheduleDto.class))
                                .collect(Collectors.toList())
                );
            }
        }

        log.debug("Converted entities to dto, the result is {}", gson.toJson(content));
        return content;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public ApiResponse get(UUID uuid) {
        if (uuid != null) {
            PaymentSchedule entity = daoService.findById(uuid);
            if (entity == null) {
                log.warn("No data found");
                return null;
            }
            return singleResponse(mapper.map(entity, PaymentScheduleDto.class));
        }
        return null;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public ApiResponse create(PaymentScheduleDto paymentScheduleDto, UserDetailsImpl principal) {
        if (paymentScheduleDto == null) {
            log.warn("param is empty");
            return errorResponse(Errors.PARAM_IS_EMPTY.name());
        }
        PaymentSchedule entity = mapper.map(paymentScheduleDto, PaymentSchedule.class);

        if (paymentScheduleDto.getLoanOffer() != null) {
            LoanOffer loanOffer = loanOfferDaoService.findByLoanOfferDto(paymentScheduleDto.getLoanOffer(), principal);
            if (loanOffer == null) {
                log.warn("loanOffer not found");
                return errorResponse(Errors.LOAN_OFFER_NOT_FOUND.name());
            }
            entity.setLoanOffer(loanOffer);
        }

        entity = daoService.save(entity);

        return singleResponse(mapper.map(entity, PaymentScheduleDto.class));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public ApiResponse update(UUID id, PaymentScheduleDto dto, UserDetailsImpl principal) {
        if (dto == null) {
            log.warn("param is empty");
            return errorResponse(Errors.PARAM_IS_EMPTY.name());
        }
        PaymentSchedule entity = daoService.findById(id);
        if (entity == null) {
            log.warn("Entity not found");
            return errorResponse(Errors.PAYMENT_SCHEDULE_NOT_FOUND.name());
        }

//        LoanOffer loanOffer = loanOfferDaoService.findByLoanOfferDto(dto.getLoanOffer(), principal);
//        if (loanOffer == null) {
//            log.warn("loanOffer not found");
//            return errorResponse(Errors.LOAN_OFFER_NOT_FOUND.name());
//        }

        entity = mapper.map(dto, PaymentSchedule.class);
        entity.setUpdatedAt(ZonedDateTime.now(Clock.systemUTC()).toInstant().toEpochMilli());
//        entity.setLoanOffer(loanOffer);

        entity = daoService.save(entity);

        return singleResponse(mapper.map(entity, PaymentScheduleDto.class));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean delete(UUID id, UUID userId) {
        PaymentSchedule entity = daoService.findById(id);
        if (entity == null) {
            log.warn("Entity with id={} not found", id.toString());
        } else {
            entity.setDeletedBy(userId);
            daoService.save(entity);

            log.debug("Deleted is completed by id {}, deleted by user {}", id, userId);
            return true;
        }
        return false;
    }

    //    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
//    public ApiResponse filter(LoanOfferDto dto, UserDetailsImpl principal) {
//
//        if (dto == null) {
//            log.debug("Param is null");
//            return errorResponse(Errors.PARAM_IS_EMPTY.name());
//        }
//
//        LoanOffer loanOffer = loanOfferDaoService.findByLoanOfferDto(dto, principal);
//        if (loanOffer == null) {
//            return ApiResponse.builder().build();
//        }
//
//        List<PaymentSchedule> entities = daoService.getAllByLoanOffer(loanOffer);
//        if (entities.isEmpty()) {
//            log.debug("The result is empty");
//            return ApiResponse.builder().build();
//        }
//
//        List<PaymentScheduleDto> content = entities.stream()
//                .map(entity -> mapper.map(entity, PaymentScheduleDto.class))
//                .collect(Collectors.toList());
//
//        log.debug("The resulting number={} entities by dto={}", content.size(),
//                gson.toJson(dto));
//        return ApiResponse.builder()
//                .content(content)
//                .totalElements(content.size())
//                .build();
//    }

}
