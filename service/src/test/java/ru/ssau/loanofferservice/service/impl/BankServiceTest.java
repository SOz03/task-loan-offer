package ru.ssau.loanofferservice.service.impl;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import ru.ssau.loanofferservice.LoanOfferApplication;
import ru.ssau.loanofferservice.dto.BankDto;
import ru.ssau.loanofferservice.dto.CreditDto;
import ru.ssau.loanofferservice.dto.UserDto;
import ru.ssau.loanofferservice.dto.enums.Role;
import ru.ssau.loanofferservice.dto.response.ApiResponse;
import ru.ssau.loanofferservice.jpa.dao.BankDaoService;
import ru.ssau.loanofferservice.jpa.entity.Bank;
import ru.ssau.loanofferservice.jpa.entity.Credit;
import ru.ssau.loanofferservice.jpa.entity.User;
import ru.ssau.loanofferservice.security.config.principal.UserDetailsImpl;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@Transactional
@RunWith(SpringRunner.class)
@SpringBootTest(classes = LoanOfferApplication.class)
class BankServiceTest {

    @Autowired
    private Gson gson;

    @Autowired
    private BankService bankService;
    @Autowired
    private BankDaoService bankDaoService;

    private CreditDto creditForCreate;
    private UserDetailsImpl userPrincipal;
    private UserDetailsImpl adminPrincipal;

    @PostConstruct
    public void init() {
        log.info("Setting up fields and running tests");
        User user1 = User.builder()
                .username("user1")
                .encryptPassword("$2a$10$oIz7xO8xM3vU4F5wO8mOAelTZk1SHJi.rjTVZT1sa22TUaqx4h8t6")
                .email("user1@mail.ru")
                .role(Role.USER)
                .build();
        user1.setId(UUID.randomUUID());
        User user2 = User.builder()
                .username("user2")
                .encryptPassword("$2a$10$oIz7xO8xM3vU4F5wO8mOAelTZk1SHJi.rjTVZT1sa22TUaqx4h8t6")
                .email("user2@mail.ru")
                .role(Role.USER)
                .build();
        user2.setId(UUID.randomUUID());

        userPrincipal = UserDetailsImpl.fromUser(user1);
        assertNotNull(userPrincipal);

        creditForCreate = CreditDto.builder()
                .rate(BigDecimal.valueOf(0L))
                .limitation(BigDecimal.valueOf(0L))
                .deadline(12L)
                .build();

        adminPrincipal = UserDetailsImpl.fromUser(
                User.builder()
                        .username("admin")
                        .encryptPassword("$2a$10$MQ6jKK6LMmJiw7hQrQMNx.UspJAe2F.d04xr3qVDlsCw8FPXVWAg.")
                        .email("admin@mail.ru")
                        .role(Role.ADMIN)
                        .build()
        );
    }

    @Test
    void select() {
        User user1 = User.builder()
                .username("user-test1")
                .encryptPassword("$2a$10$oIz7xO8xM3vU4F5wO8mOAelTZk1SHJi.rjTVZT1sa22TUaqx4h8t6")
                .email("user1@mail.ru")
                .role(Role.USER)
                .build();

        Credit credit = Credit.builder()
                .limitation(BigDecimal.valueOf(1_000_000L))
                .deadline(24L)
                .rate(BigDecimal.valueOf(5.00))
                .build();

        List<User> users = new ArrayList<>();
        users.add(user1);

        Bank bank = Bank.builder()
                .bankName("new bank")
                .credits(Collections.singletonList(credit))
                .users(users)
                .build();
        bank.setId(UUID.randomUUID());
        bankDaoService.save(bank);

        List<BankDto> bankDtoList = bankService.select(userPrincipal);
        BankDto bankDto = bankDtoList.stream()
                .filter(dto -> dto.getBankName().equals(bank.getBankName()))
                .findFirst().orElse(null);
        assertNotNull(bankDto);
        assertNotNull(bank.getId());
        assertNotNull(bank.getBankName());
        assertNotNull(bank.getCredits());
        assertNotNull(bank.getUsers());
        assertNotNull(bank.toString());

        bankDtoList = bankService.select(adminPrincipal);
        assertEquals(1, bankDtoList.get(0).getUsers().size());

        log.info("Testing of the select method is complete");
    }

    @Test
    void get() {
        Bank bank = Bank.builder().bankName("bank")
                .build();
        bank = bankDaoService.save(bank);

        ApiResponse apiResponse = bankService.get(bank.getId());

        assertEquals(1, apiResponse.getContent().size());
        BankDto check = (BankDto) apiResponse.getContent().get(0);

        assertEquals(check.getBankName(), bank.getBankName());
    }

    @Test
    void create() {
        ApiResponse response = bankService.create(new BankDto(), adminPrincipal);
        assertNotNull(response.getErrorMessage());

        BankDto bankDto = BankDto.builder().bankName("  ").build();
        response = bankService.create(bankDto, adminPrincipal);
        assertNotNull(response.getErrorMessage());

        bankDto = BankDto.builder().bankName("bank-test-create").build();
        bankDto.setId(UUID.fromString("3203659e-b0f9-11ed-afa1-0242ac120002"));

        bankDto.setCredits(Collections.singletonList(creditForCreate));
        response = bankService.create(bankDto, adminPrincipal);
        assertNotNull(response.getContent());

    }

    @Test
    void update() {
        ApiResponse response = bankService.update(
                UUID.fromString("3203659e-b0f9-11ed-afa1-0242ac120003"), null, adminPrincipal
        );
        assertNotNull(response.getErrorMessage());

        response = bankService.update(
                UUID.fromString("3203659e-b0f9-11ed-afa1-0242ac120003"), new BankDto(), adminPrincipal
        );
        assertNotNull(response.getErrorMessage());

        BankDto bankDto = BankDto.builder().bankName("bank-test-update").build();
        Bank bank = bankDaoService.save(Bank.builder().bankName("safsafsafaf").build());
        response = bankService.update(bank.getId(), bankDto, adminPrincipal);
        assertNotNull(response.getContent());
        BankDto dto = (BankDto) response.getContent().get(0);
        assertEquals(bankDto.getBankName(), dto.getBankName());
    }

    @Test
    void delete() {
        boolean deleted = bankService.delete(UUID.randomUUID(), UUID.randomUUID());
        assertFalse(deleted);

        Bank bank = Bank.builder().bankName("bank-test-delete").build();
        bank = bankDaoService.save(bank);

        deleted = bankService.delete(bank.getId(), UUID.randomUUID());
        assertTrue(deleted);
    }

    @Test
    void findUsers() {
        BankDto bank = BankDto.builder()
                .bankName("bank-test-delete")
                .users(Collections.singletonList(
                        UserDto.builder().username("test-find-users").email("test-find-users").build()))
                .build();
        List<User> users = bankService.findUsers(bank);
        assertEquals(0, users.size());
    }
}