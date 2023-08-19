package ru.ssau.loanofferservice.service.impl;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.ssau.loanofferservice.LoanOfferApplication;
import ru.ssau.loanofferservice.dto.CreditDto;
import ru.ssau.loanofferservice.dto.enums.Role;
import ru.ssau.loanofferservice.dto.response.ApiResponse;
import ru.ssau.loanofferservice.dto.response.Errors;
import ru.ssau.loanofferservice.jpa.dao.CreditDaoService;
import ru.ssau.loanofferservice.jpa.dao.LoanOfferDaoService;
import ru.ssau.loanofferservice.jpa.entity.Credit;
import ru.ssau.loanofferservice.jpa.entity.LoanOffer;
import ru.ssau.loanofferservice.jpa.entity.User;
import ru.ssau.loanofferservice.security.config.principal.UserDetailsImpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Slf4j
@SpringBootTest(classes = LoanOfferApplication.class)
class CreditServiceTest {

    @Autowired
    private Gson gson;
    @Autowired
    private ModelMapper mapper;

    private UserDetailsImpl userPrincipal;
    private UserDetailsImpl adminPrincipal;

    @Before
    public void init() {
        log.info("Setting up fields and running tests");

        userPrincipal = UserDetailsImpl.fromUser(
                User.builder()
                        .username("user")
                        .encryptPassword("$2a$10$oIz7xO8xM3vU4F5wO8mOAelTZk1SHJi.rjTVZT1sa22TUaqx4h8t6")
                        .email("user@mail.ru")
                        .role(Role.USER)
                        .build()
        );

        adminPrincipal = UserDetailsImpl.fromUser(
                User.builder()
                        .username("admin")
                        .encryptPassword("$2a$10$MQ6jKK6LMmJiw7hQrQMNx.UspJAe2F.d04xr3qVDlsCw8FPXVWAg.")
                        .email("admin@mail.ru")
                        .role(Role.ADMIN)
                        .build()
        );

        assertEquals(Role.ADMIN, Role.fromValue("ADMIN"));
        assertEquals(Role.USER, Role.fromValue("USER"));
    }

    @Test
    public void select() {
        CreditDaoService daoService = mock(CreditDaoService.class);
        final CreditService creditService = new CreditService(
                gson,
                mapper,
                daoService,
                mock(LoanOfferService.class),
                mock(LoanOfferDaoService.class)
        );

        Credit credit = Credit.builder()
                .limitation(BigDecimal.valueOf(1_000_000L))
                .deadline(24L)
                .rate(BigDecimal.valueOf(5.00))
                .build();
        when(daoService.findAll()).thenReturn(Collections.singletonList(credit));

        List<CreditDto> content = creditService.select(userPrincipal);
        assertEquals(1, content.size());
        assertEquals(1_000_000L, content.get(0).getLimitation().longValue());
        assertEquals(5.00, content.get(0).getRate().doubleValue());
        assertEquals(24L, content.get(0).getDeadline());

        when(daoService.findAll()).thenReturn(new ArrayList<>());
        content = creditService.select(userPrincipal);
        assertEquals(content.size(), 0);
        log.info("Testing of the select method is complete");
    }

    @Test
    public void create() {
        CreditDaoService daoService = mock(CreditDaoService.class);
        CreditService creditService = new CreditService(
                gson,
                mapper,
                daoService,
                mock(LoanOfferService.class),
                mock(LoanOfferDaoService.class)
        );

        CreditDto dto = CreditDto.builder()
                .limitation(BigDecimal.valueOf(1_000_000L))
                .deadline(24L)
                .rate(BigDecimal.valueOf(5.00))
                .build();

        Credit saved = mapper.map(dto, Credit.class);
        saved.setId(UUID.randomUUID());
        saved.setUpdatedAt(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
        saved.setCreatedAt(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));

        given(daoService.save(any(Credit.class))).willReturn(saved);

        ApiResponse response = creditService.create(dto, userPrincipal);
        assertEquals(1, response.getContent().size());

        CreditDto responseContent = (CreditDto) response.getContent().get(0);
        assertEquals(responseContent.getId(), saved.getId());
        assertEquals(responseContent.getLimitation(), saved.getLimitation());
        log.info("Testing of the select method is complete");
        assertNotNull(responseContent.toString());
        assertNotNull(saved.toString());
    }

    @Test
    public void get() {
        CreditDaoService daoService = mock(CreditDaoService.class);
        final CreditService creditService = new CreditService(
                gson,
                mapper,
                daoService,
                mock(LoanOfferService.class),
                mock(LoanOfferDaoService.class)
        );

        Credit credit = Credit.builder()
                .limitation(BigDecimal.valueOf(1_000_000L))
                .deadline(24L)
                .rate(BigDecimal.valueOf(5.00))
                .build();
        UUID uuid = UUID.fromString("cac41e54-97e0-11ed-a8fc-0242ac120002");
        credit.setId(uuid);

        when(daoService.findById(any(UUID.class))).thenReturn(credit);

        ApiResponse response = creditService.get(uuid);
        assertEquals(1, response.getContent().size());

        CreditDto responseContent = (CreditDto) response.getContent().get(0);
        assertEquals(responseContent.getId(), credit.getId());
        assertEquals(responseContent.getLimitation(), credit.getLimitation());
        assertEquals(responseContent.getRate(), credit.getRate());
        assertEquals(responseContent.getDeadline(), credit.getDeadline());

        when(daoService.findById(any(UUID.class))).thenReturn(null);
        response = creditService.get(uuid);
        assertEquals(Errors.valueOf(response.getErrorMessage()), Errors.CREDIT_NOT_FOUND);
        assertEquals(response.getTotalElements(), 0);
        log.info("Testing of the select method is complete");
    }


    @Test
    public void update() {
        CreditDaoService daoService = mock(CreditDaoService.class);
        CreditService creditService = new CreditService(
                gson,
                mapper,
                daoService,
                mock(LoanOfferService.class),
                mock(LoanOfferDaoService.class)
        );

        CreditDto dto = CreditDto.builder()
                .limitation(BigDecimal.valueOf(1_000_000L))
                .deadline(24L)
                .rate(BigDecimal.valueOf(5.00))
                .build();

        Credit credit = mapper.map(dto, Credit.class);
        UUID uuid = UUID.fromString("cac41e54-97e0-11ed-a8fc-0242ac120002");
        credit.setId(uuid);
        credit.setUpdatedAt(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
        credit.setCreatedAt(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));

        ApiResponse response = creditService.update(uuid, null, userPrincipal);
        assertEquals(response.getErrorMessage(), Errors.PARAM_IS_EMPTY.name());

        when(daoService.findById(any(UUID.class))).thenReturn(credit);
        given(daoService.save(any(Credit.class))).willReturn(credit);

        response = creditService.update(uuid, dto, userPrincipal);
        CreditDto responseContent = (CreditDto) response.getContent().get(0);
        assertEquals(1, response.getContent().size());

        when(daoService.findById(any(UUID.class))).thenReturn(null);
        response = creditService.get(uuid);
        assertEquals(Errors.valueOf(response.getErrorMessage()), Errors.CREDIT_NOT_FOUND);
        assertEquals(response.getTotalElements(), 0);
        log.info("Testing of the select method is complete");
    }

    @Test
    public void delete() {
        LoanOfferService loanOfferService = mock(LoanOfferService.class);
        LoanOfferDaoService loanOfferDaoService = mock(LoanOfferDaoService.class);
        CreditDaoService daoService = mock(CreditDaoService.class);
        CreditService creditService = new CreditService(
                gson,
                mapper,
                daoService,
                loanOfferService,
                loanOfferDaoService
        );
        UUID uuid = UUID.fromString("cac41e54-97e0-11ed-a8fc-0242ac120002");
        UUID userId = UUID.fromString("2f8af45c-97e6-11ed-a8fc-0242ac120002");

        when(daoService.findById(any(UUID.class))).thenReturn(null);
        creditService.delete(uuid, userId);

        Credit credit = Credit.builder()
                .limitation(BigDecimal.valueOf(1_000_000L))
                .deadline(24L)
                .rate(BigDecimal.valueOf(5.00))
                .build();
        credit.setId(uuid);

        when(daoService.findById(any(UUID.class))).thenReturn(credit);
        LoanOffer loanOffer = LoanOffer.builder()
                .credit(credit)
                .build();
        when(loanOfferDaoService.findAllByCredit(any(Credit.class)))
                .thenReturn(Collections.singletonList(loanOffer));

        when(loanOfferService.delete(any(UUID.class), any(UUID.class)))
                .thenReturn(true);

        when(daoService.save(any(Credit.class)))
                .thenReturn(null);

        creditService.delete(uuid, userId);
    }
}