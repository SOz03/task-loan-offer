package ru.ssau.loanofferservice.service.impl;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.ssau.loanofferservice.dto.BankDto;
import ru.ssau.loanofferservice.dto.CreditDto;
import ru.ssau.loanofferservice.dto.UserDto;
import ru.ssau.loanofferservice.dto.enums.Role;
import ru.ssau.loanofferservice.dto.response.ApiResponse;
import ru.ssau.loanofferservice.dto.response.Errors;
import ru.ssau.loanofferservice.jpa.dao.BankDaoService;
import ru.ssau.loanofferservice.jpa.dao.CreditDaoService;
import ru.ssau.loanofferservice.jpa.dao.LoanOfferDaoService;
import ru.ssau.loanofferservice.jpa.dao.UserDaoService;
import ru.ssau.loanofferservice.jpa.entity.Bank;
import ru.ssau.loanofferservice.jpa.entity.Credit;
import ru.ssau.loanofferservice.jpa.entity.LoanOffer;
import ru.ssau.loanofferservice.jpa.entity.User;
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
public class BankService implements ApiService<BankDto> {

    private final Gson gson;
    private final ModelMapper mapper;
    private final BankDaoService bankDaoService;
    private final UserDaoService userDaoService;
    private final CreditDaoService creditDaoService;
    private final LoanOfferService loanOfferService;
    private final LoanOfferDaoService loanOfferDaoService;

    private BankService self;

    @Autowired
    public void setSelf(BankService bankService) {
        this.self = bankService;
    }

    @Override
    public List<BankDto> select(UserDetailsImpl principal) {
        log.debug("Search for all bank entities");
        List<Bank> entities = (List<Bank>) bankDaoService.findAll();

        entities = zeroUsersForTheUserRole(entities, principal);

        List<BankDto> content = entities.stream()
                .map(entity -> mapper.map(entity, BankDto.class))
                .collect(Collectors.toList());

        log.debug("Converted entities to dto, the result is {}", gson.toJson(content));
        return content;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public ApiResponse get(UUID uuid) {
        log.debug("Entity search by id={}", uuid.toString());
        Bank entity = bankDaoService.findById(uuid);
        if (entity == null) {
            return ApiResponse.builder().build();
        }
        return singleResponse(mapper.map(entity, BankDto.class));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public ApiResponse create(BankDto bankDto, UserDetailsImpl principal) {
        log.debug("Create new entity={}", gson.toJson(bankDto));

        if (bankDto != null) {
            if (StringUtils.isBlank(bankDto.getBankName())) {
                return errorResponse(Errors.BANK_NAME_IS_EMPTY.name());
            }
            Bank bank = new Bank(bankDto.getBankName());

            List<Credit> credits = self.findCredits(bankDto);
            bank.setCredits(credits);

            bank = bankDaoService.save(bank);
            bankDto = mapper.map(bank, BankDto.class);

            log.debug("Saving entity={} is completed", gson.toJson(bankDto));
            return singleResponse(bankDto);
        }
        log.debug("Dto is empty");
        return errorResponse(Errors.BANK_IS_EMPTY.name());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public ApiResponse update(UUID id, BankDto dto, UserDetailsImpl principal) {
        log.debug("Update entity with id={} and dto={}", id.toString(), gson.toJson(dto));
        if (dto == null) {
            log.warn("Param is empty");
            return errorResponse(Errors.BANK_IS_EMPTY.name());
        }

        Bank bank = bankDaoService.findById(id);
        if (bank == null) {
            log.warn("Entity with id={} not found", id);
            return errorResponse(Errors.BANK_IS_EMPTY.name());
        }

        List<Credit> credits = self.findCredits(dto);
        bank.setCredits(credits);

        bank.setBankName(dto.getBankName());
        bank.setUpdatedAt(ZonedDateTime.now(Clock.systemUTC()).toInstant().toEpochMilli());
        bank = bankDaoService.save(bank);
        log.debug("Saving completed {}", gson.toJson(dto));

        return singleResponse(mapper.map(bank, BankDto.class));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean delete(UUID id, UUID userId) {
        Bank bank = bankDaoService.findById(id);
        if (bank == null) {
            log.warn("Entity with id={} not found", id.toString());
        } else {
            List<LoanOffer> loanOfferList = loanOfferDaoService.findAllByBank(bank);
            if (!loanOfferList.isEmpty()) {
                loanOfferList.forEach(loanOffer -> {
                    loanOfferService.delete(loanOffer.getId(), userId);
                });
            }

            bank.setDeletedBy(userId);
            bankDaoService.save(bank);

            log.debug("Deleted is completed by id {}", id);
            return true;
        }
        return false;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<Credit> findCredits(BankDto dto) {
        List<Credit> credits = new ArrayList<>();
        if (dto.getCredits() != null && !dto.getCredits().isEmpty()) {
            for (CreditDto creditDto : dto.getCredits()) {
                Credit credit = creditDaoService.findByCreditDto(creditDto);
                if (credit != null) {
                    credits.add(credit);
                } else {
                    // TRACE
                    log.warn("Credit not found dto={}", gson.toJson(creditDto));
                }
            }
        }
        return credits;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<User> findUsers(BankDto dto) {
        List<User> users = new ArrayList<>();
        if (dto.getUsers() != null && !dto.getUsers().isEmpty()) {
            for (UserDto userDto : dto.getUsers()) {
                User user = userDaoService.findByUsernameOrEmail(userDto.getUsername(), userDto.getEmail());
                if (user != null) {
                    users.add(user);
                } else {
                    // TRACE
                    log.warn("User not found dto={}", gson.toJson(userDto));
                }
            }
        }
        return users;
    }

    private List<Bank> zeroUsersForTheUserRole(List<Bank> banks, UserDetailsImpl principal) {
        if (principal.getAuthorities().stream()
                .filter(role -> role.getAuthority().equals(Role.USER.name()))
                .count() == 1) {
            banks = banks.stream()
                    .peek(bank -> {
                                List<User> users = bank.getUsers().stream()
                                        .filter(user -> user.getUsername().equals(principal.getUsername()))
                                        .collect(Collectors.toList());
                                bank.setUsers(users);
                            }
                    ).collect(Collectors.toList());
        }
        return banks;
    }
}
