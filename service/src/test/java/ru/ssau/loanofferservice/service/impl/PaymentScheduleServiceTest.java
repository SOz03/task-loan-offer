package ru.ssau.loanofferservice.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import ru.ssau.loanofferservice.LoanOfferApplication;
import ru.ssau.loanofferservice.dto.LoanOfferDto;
import ru.ssau.loanofferservice.dto.PaymentScheduleDto;
import ru.ssau.loanofferservice.dto.enums.Role;
import ru.ssau.loanofferservice.dto.response.ApiResponse;
import ru.ssau.loanofferservice.jpa.dao.*;
import ru.ssau.loanofferservice.jpa.entity.*;
import ru.ssau.loanofferservice.security.config.principal.UserDetailsImpl;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@Transactional
@RunWith(SpringRunner.class)
@SpringBootTest(classes = LoanOfferApplication.class)
class PaymentScheduleServiceTest {

    @Autowired
    private PaymentScheduleService paymentScheduleService;
    @Autowired
    private PaymentScheduleDaoService paymentScheduleDaoService;
    @Autowired
    private ModelMapper mapper;
    @Autowired
    private LoanOfferDaoService loanOfferDaoService;
    @Autowired
    private UserDaoService userDaoService;
    @Autowired
    private BankDaoService bankDaoService;
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
        PaymentSchedule paymentSchedule = PaymentSchedule.builder()
                .datePayment(LocalDate.now())
                .interest(BigDecimal.valueOf(1L))
                .amount(BigDecimal.valueOf(2L))
                .body(BigDecimal.valueOf(3L))
                .build();
        User user = User.builder()
                .role(Role.USER).username("user10101").encryptPassword("proms").email("mailcom01@o.ru")
                .build();
        User user1 = User.builder()
                .role(Role.USER).username("user10201").encryptPassword("proms").email("mailcom101@o.ru")
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
                .bankName("BANK_SELECT_PAYMENT")
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


        loanOffer = loanOfferDaoService.save(loanOffer);
        paymentSchedule.setLoanOffer(loanOffer);

        paymentSchedule = paymentScheduleDaoService.save(paymentSchedule);

        List<PaymentScheduleDto> response = paymentScheduleService.select(userPrincipal);
        paymentScheduleService.select(adminPrincipal);
    }

    @Test
    void get() {
        PaymentSchedule paymentSchedule = PaymentSchedule.builder()
                .datePayment(LocalDate.now())
                .interest(BigDecimal.valueOf(1L))
                .amount(BigDecimal.valueOf(2L))
                .body(BigDecimal.valueOf(3L))
                .build();
        User user = User.builder()
                .role(Role.USER).username("user1010").encryptPassword("proms").email("mailcom0@o.ru")
                .build();
        User user1 = User.builder()
                .role(Role.USER).username("user1020").encryptPassword("proms").email("mailcom10@o.ru")
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
                .bankName("BANK_GET_PAYMENT")
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

        loanOffer = loanOfferDaoService.save(loanOffer);
        paymentSchedule.setLoanOffer(loanOffer);

        ApiResponse response = paymentScheduleService.get(null);
        assertNull(response);

        response = paymentScheduleService.get(UUID.randomUUID());
        assertNull(response);

        paymentSchedule = paymentScheduleDaoService.save(paymentSchedule);
        response = paymentScheduleService.get(paymentSchedule.getId());
        PaymentScheduleDto dto = (PaymentScheduleDto) response.getContent().get(0);
        assertNotNull(dto);
        assertEquals(paymentSchedule.getDatePayment(), dto.getDatePayment());
        assertEquals(paymentSchedule.getAmount(), dto.getAmount());
        assertEquals(paymentSchedule.getBody(), dto.getBody());
        assertEquals(paymentSchedule.getLoanOffer().getId(), dto.getLoanOffer().getId());
    }

    @Test
    void create() {
        ApiResponse response = paymentScheduleService.create(null, adminPrincipal);
        assertNotNull(response.getErrorMessage());

        PaymentScheduleDto dto = PaymentScheduleDto.builder()
                .datePayment(LocalDate.now())
                .interest(BigDecimal.valueOf(1L))
                .amount(BigDecimal.valueOf(2L))
                .body(BigDecimal.valueOf(3L))
                .build();

        User user = User.builder()
                .role(Role.USER).username("user101").encryptPassword("proms").email("mailcom@o.ru")
                .build();
        User user1 = User.builder()
                .role(Role.USER).username("user102").encryptPassword("proms").email("mailcom1@o.ru")
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
                .bankName("BANK_CREATE_PAYMENT")
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

        loanOffer = loanOfferDaoService.save(loanOffer);

        LoanOfferDto loanOfferDto = mapper.map(loanOffer, LoanOfferDto.class);
        dto.setLoanOffer(loanOfferDto);
        response = paymentScheduleService.create(dto, adminPrincipal);
    }

    @Test
    void update() {
        PaymentSchedule paymentSchedule = PaymentSchedule.builder()
                .datePayment(LocalDate.now())
                .interest(BigDecimal.valueOf(1L))
                .amount(BigDecimal.valueOf(2L))
                .body(BigDecimal.valueOf(3L))
                .build();
        User user = User.builder()
                .role(Role.USER).username("user10107").encryptPassword("proms").email("mailcom07@o.ru")
                .build();
        User user1 = User.builder()
                .role(Role.USER).username("user10207").encryptPassword("proms").email("mailcom107@o.ru")
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
                .bankName("BANK_CREATE_PAYMENT")
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

        loanOffer = loanOfferDaoService.save(loanOffer);
        paymentSchedule.setLoanOffer(loanOffer);

        paymentSchedule = paymentScheduleDaoService.save(paymentSchedule);
        ApiResponse response = paymentScheduleService.update(paymentSchedule.getId(), null, adminPrincipal);
        assertNotNull(response.getErrorMessage());

        response = paymentScheduleService.update(UUID.randomUUID(), PaymentScheduleDto.builder().build(), adminPrincipal);
        assertNotNull(response.getErrorMessage());

        response = paymentScheduleService.update(paymentSchedule.getId(),
                PaymentScheduleDto.builder()
                        .datePayment(LocalDate.now())
                        .interest(BigDecimal.valueOf(7L))
                        .amount(BigDecimal.valueOf(7L))
                        .body(BigDecimal.valueOf(7L))
                        .loanOffer(mapper.map(loanOffer, LoanOfferDto.class))
                        .build(), adminPrincipal);
        assertEquals(1, response.getContent().size());
        PaymentScheduleDto dto = (PaymentScheduleDto) response.getContent().get(0);
        assertNotNull(dto);
        assertEquals(BigDecimal.valueOf(7L), dto.getAmount());
        assertEquals(BigDecimal.valueOf(7L), dto.getInterest());
        assertEquals(BigDecimal.valueOf(7L), dto.getBody());

        paymentScheduleService.update(paymentSchedule.getId(), null, adminPrincipal);
    }

    @Test
    void delete() {
        PaymentSchedule paymentSchedule = PaymentSchedule.builder()
                .datePayment(LocalDate.now())
                .interest(BigDecimal.valueOf(1L))
                .amount(BigDecimal.valueOf(2L))
                .body(BigDecimal.valueOf(3L))
                .build();
        User user = User.builder()
                .role(Role.USER).username("user10107d").encryptPassword("proms").email("mailcom07d@o.ru")
                .build();
        User user1 = User.builder()
                .role(Role.USER).username("user10207d").encryptPassword("proms").email("mailcom107d@o.ru")
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
                .bankName("BANK_DELETE_PAYMENT")
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

        loanOffer = loanOfferDaoService.save(loanOffer);
        paymentSchedule.setLoanOffer(loanOffer);

        paymentSchedule = paymentScheduleDaoService.save(paymentSchedule);
        paymentScheduleService.delete(paymentSchedule.getId(), UUID.randomUUID());

        paymentScheduleService.delete(UUID.randomUUID(), UUID.randomUUID());
    }
}