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
import ru.ssau.loanofferservice.dto.CreditDto;
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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = LoanOfferApplication.class)
class CreditControllerTest {

    private Authentication authentication;

    @Autowired
    private CreditController creditController;
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
        ResponseEntity<?> response = creditController.select(authentication);
        assertNotNull(response.getBody());
        List<CreditDto> list = (List<CreditDto>) response.getBody();
        assertNotNull(list);
    }

    @Test
    void get() {
        ResponseEntity<?> response = creditController.get(null, authentication);
        ApiResponse api = (ApiResponse) response.getBody();
        assertNotNull(api);
        assertNotNull(api.getErrorMessage());
    }

    @Test
    void create() {
        ResponseEntity<?> response = creditController.create(null, authentication);
        ApiResponse api = (ApiResponse) response.getBody();
        assertNull(api);
    }

    @Test
    void update() {
        ResponseEntity<?> response = creditController.update(UUID.randomUUID(), null, authentication);
        ApiResponse api = (ApiResponse) response.getBody();
        assertNotNull(api);
        assertNotNull(api.getErrorMessage());
    }

    @Test
    void delete() {
        ResponseEntity<?> response = creditController.delete(UUID.randomUUID(), authentication);
        ApiResponse api = (ApiResponse) response.getBody();
        assertNull(api);
    }
}