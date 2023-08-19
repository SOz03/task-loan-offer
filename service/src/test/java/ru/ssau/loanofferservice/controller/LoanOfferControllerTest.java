package ru.ssau.loanofferservice.controller;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringRunner;
import ru.ssau.loanofferservice.LoanOfferApplication;
import ru.ssau.loanofferservice.dto.BankDto;
import ru.ssau.loanofferservice.dto.LoanOfferDto;
import ru.ssau.loanofferservice.dto.enums.Role;
import ru.ssau.loanofferservice.dto.response.ApiResponse;
import ru.ssau.loanofferservice.jpa.dao.UserDaoService;
import ru.ssau.loanofferservice.jpa.entity.User;
import ru.ssau.loanofferservice.security.config.principal.UserDetailsImpl;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = LoanOfferApplication.class)
class LoanOfferControllerTest {

    private Authentication authentication;

    @Autowired
    private LoanOfferController loanOfferController;
    @Autowired
    private UserDaoService userDaoService;

    @PostConstruct
    public void init() {
        User user2 = userDaoService.findByUsernameOrEmail("user-credit", "user-credit@mail.ru");
        if (user2 == null) {
            user2 = User.builder()
                    .username("user-credit")
                    .encryptPassword("$2a$10$oIz7xO8xM3vU4F5wO8mOAelTZk1SHJi.rjTVZT1sa22TUaqx4h8t6")
                    .email("user-credit@mail.ru")
                    .role(Role.USER)
                    .build();
            user2 = userDaoService.save(user2);
        }

        UserDetailsImpl userPrincipal = UserDetailsImpl.fromUser(user2);

        authentication = new Authentication() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return Collections.singletonList(new SimpleGrantedAuthority(Role.ADMIN.toString()));
            }

            @Override
            public Object getCredentials() {
                return null;
            }

            @Override
            public Object getDetails() {
                return null;
            }

            @Override
            public Object getPrincipal() {
                return userPrincipal;
            }

            @Override
            public boolean isAuthenticated() {
                return true;
            }

            @Override
            public void setAuthenticated(boolean b) throws IllegalArgumentException {

            }

            @Override
            public String getName() {
                return userPrincipal.getFullname();
            }
        };
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    @Transactional
    public void select() {
        ResponseEntity<?> response = loanOfferController.select(authentication);
        assertNotNull(response.getBody());
        List<LoanOfferDto> list = (List<LoanOfferDto>) response.getBody();
        assertNotNull(list);
    }

    @Test
    void get() {
        ResponseEntity<?> response = loanOfferController.get(UUID.randomUUID(), authentication);
        ApiResponse api = (ApiResponse) response.getBody();
        assertNotNull(api);
    }

    @Test
    void create() {
        ResponseEntity<?> response = loanOfferController.create(null, authentication);
        ApiResponse api = (ApiResponse) response.getBody();
        assertNotNull(api);
    }

    @Test
    void delete() {
        ResponseEntity<?> response = loanOfferController.delete(UUID.randomUUID(), authentication);
        ApiResponse api = (ApiResponse) response.getBody();
        assertNull(api);
    }
}