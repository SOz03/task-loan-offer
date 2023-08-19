package ru.ssau.loanofferservice.controller;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import ru.ssau.loanofferservice.LoanOfferApplication;
import ru.ssau.loanofferservice.dto.LoginDto;
import ru.ssau.loanofferservice.dto.UserDto;
import ru.ssau.loanofferservice.dto.enums.Role;
import ru.ssau.loanofferservice.dto.response.ApiResponse;
import ru.ssau.loanofferservice.jpa.dao.UserDaoService;
import ru.ssau.loanofferservice.jpa.entity.User;

import javax.annotation.PostConstruct;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = LoanOfferApplication.class)
class AuthenticationControllerTest {

    @Autowired
    private UserDaoService userDaoService;
    @Autowired
    private AuthenticationController authenticationController;

    private static final String USERNAME_IN = "user-credit";
    private static final String EMAIL_IN = "user-credit@mail.ru";

    private static final String USERNAME = "user-auth-check";
    private static final String EMAIL = "user-auth-check@mail.ru";

    @PostConstruct
    public void init() {
        User user2 = userDaoService.findByUsernameOrEmail(USERNAME_IN, EMAIL_IN);
        if (user2 == null) {
            user2 = User.builder()
                    .username(USERNAME_IN)
                    .encryptPassword("$2a$10$oIz7xO8xM3vU4F5wO8mOAelTZk1SHJi.rjTVZT1sa22TUaqx4h8t6")
                    .email(EMAIL_IN)
                    .role(Role.USER)
                    .build();
            user2 = userDaoService.save(user2);
        }
    }

    @Test
    void authenticationUser() {
        LoginDto loginDto = LoginDto.builder()
                .username(USERNAME_IN)
                .password("user")
                .build();
        ResponseEntity<?> response = authenticationController.authenticationUser(loginDto);
        response = (ResponseEntity<?>) response.getBody();
        assertNotNull(response);
        LoginDto content = (LoginDto) response.getBody();
        assertNotNull(content);
        assertNotNull(content.getToken());
    }

    @Test
    void registration() {
        UserDto userDto = UserDto.builder().build();
        ResponseEntity<?> response = authenticationController.registration(userDto);
        response = (ResponseEntity<?>) response.getBody();
        assertNotNull(response);
        ApiResponse content = (ApiResponse) response.getBody();
        assertNotNull(content);
        assertNotNull(content.getErrorMessage());

        userDto.setUsername(USERNAME);
        response = authenticationController.registration(userDto);
        response = (ResponseEntity<?>) response.getBody();
        assertNotNull(response);
        content = (ApiResponse) response.getBody();
        assertNotNull(content);
        assertNotNull(content.getErrorMessage());

        userDto.setPassword("sgsga");
        response = authenticationController.registration(userDto);
        response = (ResponseEntity<?>) response.getBody();
        assertNotNull(response);
        content = (ApiResponse) response.getBody();
        assertNotNull(content);
        assertNotNull(content.getErrorMessage());

        userDto.setEmail(EMAIL);
        response = authenticationController.registration(userDto);
        response = (ResponseEntity<?>) response.getBody();
        assertNotNull(response);
        content = (ApiResponse) response.getBody();
        assertNotNull(content);
    }
}