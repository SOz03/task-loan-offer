package ru.ssau.loanofferservice.service.impl;

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
import ru.ssau.loanofferservice.dto.LoanOfferDto;
import ru.ssau.loanofferservice.dto.UserDto;
import ru.ssau.loanofferservice.dto.enums.Role;
import ru.ssau.loanofferservice.dto.response.ApiResponse;
import ru.ssau.loanofferservice.jpa.dao.BankDaoService;
import ru.ssau.loanofferservice.jpa.dao.CreditDaoService;
import ru.ssau.loanofferservice.jpa.dao.LoanOfferDaoService;
import ru.ssau.loanofferservice.jpa.dao.UserDaoService;
import ru.ssau.loanofferservice.jpa.entity.Bank;
import ru.ssau.loanofferservice.jpa.entity.Credit;
import ru.ssau.loanofferservice.jpa.entity.LoanOffer;
import ru.ssau.loanofferservice.jpa.entity.User;
import ru.ssau.loanofferservice.security.config.principal.UserDetailsImpl;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
@Transactional
@RunWith(SpringRunner.class)
@SpringBootTest(classes = LoanOfferApplication.class)
class LoanOfferServiceTest {

    @Autowired
    private LoanOfferService loanOfferService;
    @Autowired
    private LoanOfferDaoService daoService;

    @Autowired
    private BankDaoService bankDaoService;
    @Autowired
    private UserDaoService userDaoService;
    @Autowired
    private CreditDaoService creditDaoService;

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

        userPrincipal = UserDetailsImpl.fromUser(user1);
        assertNotNull(userPrincipal);

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
        loanOfferService.select(userPrincipal);

        loanOfferService.select(adminPrincipal);
    }

    @Test
    void getAndCreate() {
        Bank bank = Bank.builder().bankName("bank").build();
        bank = bankDaoService.save(bank);
        assertNotNull(bank);

        User user = User.builder()
                .role(Role.USER).username("user12").encryptPassword("prom").email("mail@o.ru")
                .build();
        user = userDaoService.save(user);
        assertNotNull(user);

        Credit credit = Credit.builder()
                .limitation(BigDecimal.valueOf(1_000_000L))
                .rate(BigDecimal.valueOf(6))
                .deadline(12L)
                .build();
        credit = creditDaoService.save(credit);
        assertNotNull(credit);

        LoanOfferDto loanOfferDto = LoanOfferDto.builder()
                .user(UserDto.builder().username(user.getUsername()).build())
                .bank(null)
                .credit(CreditDto.builder().id(credit.getId()).build())
                .build();

        ApiResponse response = loanOfferService.create(null, adminPrincipal);
        assertNotNull(response);
        assertNotNull(response.getErrorMessage());

        bank = bankDaoService.save(
                Bank.builder()
                        .bankName("bank-loan")
                        .credits(Collections.singletonList(credit))
                        .users(Collections.singletonList(user))
                        .build()
        );
        loanOfferDto.setBank(BankDto.builder().id(bank.getId()).build());

        response = loanOfferService.create(loanOfferDto, adminPrincipal);
        assertNotNull(response);
        LoanOfferDto dto = (LoanOfferDto) response.getContent().get(0);
        assertNotNull(dto);
        assertEquals(credit.getDeadline(), dto.getCredit().getDeadline());
        assertEquals(credit.getRate(), dto.getCredit().getRate());
        assertEquals(credit.getLimitation(), dto.getCredit().getLimitation());

        response = loanOfferService.create(loanOfferDto, adminPrincipal);
        assertNotNull(response);
        assertNotNull(response.getErrorMessage());

        LoanOfferDto loanOfferDto1 = (LoanOfferDto) response.getContent().get(0);
        assertNotNull(loanOfferDto1);

        response = loanOfferService.get(loanOfferDto1.getId());
    }

    @Test
    void delete() {
        loanOfferService.delete(UUID.randomUUID(), adminPrincipal.getId());
        User user = User.builder()
                .role(Role.USER).username("user-loanoffer1").encryptPassword("proms").email("user-loanoffer1@o.ru")
                .build();
        User user1 = User.builder()
                .role(Role.USER).username("user-loanoffer2").encryptPassword("proms").email("user-loanoffer2@o.ru")
                .build();
        user = userDaoService.save(user);
        Credit credit = Credit.builder()
                .limitation(BigDecimal.valueOf(1L))
                .rate(BigDecimal.valueOf(1))
                .deadline(1L)
                .build();
        credit = creditDaoService.save(credit);

        List<User> userList = new ArrayList<>();
        userList.add(user);
        userList.add(user1);

        Bank bank = Bank.builder()
                .bankName("BANK_LOAN_OFFER")
                .credits(Collections.singletonList(credit))
                .users(userList)
                .build();
        bank.setId(UUID.randomUUID());
        bank = bankDaoService.save(bank);

        LoanOffer loanOffer = LoanOffer.builder()
                .user(user)
                .loanAmount(BigDecimal.valueOf(0L))
                .bank(bank)
                .credit(credit)
                .build();

        loanOffer = daoService.save(loanOffer);

        loanOfferService.delete(loanOffer.getId(), adminPrincipal.getId());
    }
}