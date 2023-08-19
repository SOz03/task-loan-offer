package ru.ssau.loanofferservice.service.impl;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.ssau.loanofferservice.dto.*;
import ru.ssau.loanofferservice.dto.enums.Role;
import ru.ssau.loanofferservice.dto.response.ApiResponse;
import ru.ssau.loanofferservice.dto.response.Errors;
import ru.ssau.loanofferservice.jpa.dao.*;
import ru.ssau.loanofferservice.jpa.entity.*;
import ru.ssau.loanofferservice.security.config.principal.UserDetailsImpl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoanOfferService implements ApiService<LoanOfferDto> {

    private final Gson gson;
    private final ModelMapper mapper;
    private final PaymentScheduleDaoService daoService;
    private final LoanOfferDaoService loanOfferDaoService;
    private final BankDaoService bankDaoService;
    private final UserDaoService userDaoService;
    private final CreditDaoService creditDaoService;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public List<LoanOfferDto> select(UserDetailsImpl principal) {
        boolean isUser = principal.getAuthorities().stream()
                .filter(role -> role.getAuthority().equals(Role.USER.name()))
                .count() == 1;

        List<LoanOfferDto> content;
        if (isUser) {
            List<LoanOffer> loanOfferList = loanOfferDaoService.findAllBySpec(principal);
            loanOfferList = loanOfferList.stream()
                    .peek(el -> el.setPaymentSchedules(daoService.getAllByLoanOffer(el)))
                    .collect(Collectors.toList());

            content = loanOfferList.stream()
                    .map(e -> mapper.map(e, LoanOfferDto.class))
                    .peek(e -> e.getPaymentSchedules().forEach(p -> p.setLoanOffer(null)))
                    .collect(Collectors.toList());
        } else {
            List<LoanOffer> loanOfferList = (List<LoanOffer>) loanOfferDaoService.findAll();

            loanOfferList = loanOfferList.stream()
                    .peek(el -> el.setPaymentSchedules(daoService.getAllByLoanOffer(el)))
                    .collect(Collectors.toList());

            content = loanOfferList.stream()
                    .map(e -> mapper.map(e, LoanOfferDto.class))
                    .peek(e -> e.getPaymentSchedules().forEach(p -> p.setLoanOffer(null)))
                    .collect(Collectors.toList());
        }

        if (content.isEmpty()) {
            content = new ArrayList<>();
        }

        return content;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public ApiResponse get(UUID uuid) {
        LoanOffer loanOffer = loanOfferDaoService.findById(uuid);

        if (loanOffer != null) {
            LoanOfferDto content = LoanOfferDto.builder()
                    .loanAmount(loanOffer.getLoanAmount())
                    .bank(mapper.map(loanOffer.getBank(), BankDto.class))
                    .user(mapper.map(loanOffer.getUser(), UserDto.class))
                    .credit(mapper.map(loanOffer.getCredit(), CreditDto.class))
                    .paymentSchedules(loanOffer.getPaymentSchedules().stream()
                            .map(el -> mapper.map(el, PaymentScheduleDto.class))
                            .collect(Collectors.toList()))
                    .build();
            return singleResponse(content);
        }
        return errorResponse(Errors.LOAN_OFFER_NOT_FOUND.name());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public ApiResponse create(LoanOfferDto dto, UserDetailsImpl principal) {
        if (dto == null) {
            log.warn("param is empty");
            return errorResponse(Errors.PARAM_IS_EMPTY.name());
        }
        User user = userDaoService.findByUsername(dto.getUser().getUsername());
        Credit credit = creditDaoService.findById(dto.getCredit().getId());
        Bank bank = bankDaoService.findById(dto.getBank().getId());

        LoanOffer existsLoanOffer = loanOfferDaoService.getLoanOffer(bank, user, credit);
        if (existsLoanOffer != null) {
            LoanOfferDto content = mapper.map(existsLoanOffer, LoanOfferDto.class);
            ApiResponse response = ApiResponse.builder()
                    .errorMessage(Errors.LOAN_OFFER_EXISTS.name())
                    .content(Collections.singletonList(content))
                    .build();
            log.debug("Loan offer exists {}", gson.toJson(content.getId()));
            return response;
        }

        if (bank.getCredits().stream().noneMatch(el -> el.getId().equals(credit.getId()))) {
            log.warn("Credit not found in bank {}", bank.getBankName());
            return errorResponse(Errors.CREDIT_NOT_FOUND.name());
        }

        boolean isUpdate = false;
        if (bank.getUsers().stream().noneMatch(el -> el.getId().equals(user.getId()))) {
            bank.getUsers().add(user);
            isUpdate = true;
        }

        if (isUpdate) {
            bankDaoService.save(bank);
        }

        LoanOffer entity = new LoanOffer(new BigDecimal(0L), user, credit, bank, Collections.emptyList());
        entity = loanOfferDaoService.save(entity);
        LoanOffer loanOffer = setNewGraphic(entity);

        BigDecimal loanAmount = new BigDecimal(0L);
        for (PaymentSchedule pay : loanOffer.getPaymentSchedules()) {
            loanAmount = loanAmount.add(pay.getAmount());
        }
        loanOffer.setLoanAmount(loanAmount);
        loanOffer = loanOfferDaoService.save(loanOffer);

        LoanOfferDto content = LoanOfferDto.builder()
                .id(loanOffer.getId())
                .loanAmount(loanOffer.getLoanAmount().setScale(2, RoundingMode.UP))
                .bank(mapper.map(bank, BankDto.class))
                .user(mapper.map(user, UserDto.class))
                .credit(mapper.map(credit, CreditDto.class))
                .paymentSchedules(loanOffer.getPaymentSchedules().stream()
                        .map(el -> mapper.map(el, PaymentScheduleDto.class))
                        .collect(Collectors.toList()))
                .build();

        return singleResponse(content);
    }

    @Override
    public ApiResponse update(UUID id, LoanOfferDto dto, UserDetailsImpl principal) {
        return null;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean delete(UUID uuid, UUID userId) {
        LoanOffer entity = loanOfferDaoService.findById(uuid);
        if (entity == null) {
            log.debug("Entity with id={} not found", uuid.toString());
            return false;
        }

        Bank bank = entity.getBank();
        if (bank != null){
            List<LoanOffer> all = loanOfferDaoService.findAllByBankAndUser(bank, entity.getUser());
            if (all != null && all.size() == 1) {
                List<User> users = bank.getUsers().stream()
                        .filter(user -> user.getId() != entity.getUser().getId())
                        .collect(Collectors.toList());
                bank.setUsers(users);
                bankDaoService.save(bank);
            }
        }

        List<PaymentSchedule> paymentSchedules = daoService.getAllByLoanOffer(entity)
                .stream()
                .peek(e -> e.setDeletedBy(userId))
                .collect(Collectors.toList());

        daoService.saveAll(paymentSchedules);

        entity.setDeletedBy(userId);
        loanOfferDaoService.save(entity);

        log.debug("Deleted is completed by id {}, deleted by user {}", uuid, userId);
        return true;
    }

    private LoanOffer setNewGraphic(LoanOffer loanOffer) {
        Credit credit = loanOffer.getCredit();

        BigDecimal limit = credit.getLimitation();
        BigDecimal amount = limit.divide(new BigDecimal(credit.getDeadline()), 2, RoundingMode.UP);
        BigDecimal amountInterest = credit.getRate()
                .multiply(new BigDecimal("0.01"))
                .multiply(amount);
        BigDecimal amountMonth = amount.add(amountInterest);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        List<PaymentSchedule> paymentSchedules = new ArrayList<>();
        for (int i = 1; i <= credit.getDeadline(); i++) {
            calendar.add(Calendar.MONTH, +1);
            PaymentSchedule paymentGraph = new PaymentSchedule(
                    LocalDateTime.ofInstant(calendar.toInstant(), ZoneId.systemDefault()).toLocalDate(),
                    amountMonth.setScale(2, RoundingMode.UP),
                    amount.setScale(2, RoundingMode.UP),
                    amountInterest.setScale(2, RoundingMode.UP),
                    loanOffer
            );
            paymentGraph = daoService.save(paymentGraph);
            paymentSchedules.add(paymentGraph);
            calendar.setTime(calendar.getTime());
        }
        loanOffer.setPaymentSchedules(paymentSchedules);
        return loanOffer;
    }


}
