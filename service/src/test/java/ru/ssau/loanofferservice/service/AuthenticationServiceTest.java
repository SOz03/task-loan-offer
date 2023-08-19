package ru.ssau.loanofferservice.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import ru.ssau.loanofferservice.LoanOfferApplication;
import ru.ssau.loanofferservice.dto.LoginDto;
import ru.ssau.loanofferservice.dto.UserDto;
import ru.ssau.loanofferservice.dto.enums.Role;
import ru.ssau.loanofferservice.dto.response.ApiResponse;
import ru.ssau.loanofferservice.jpa.dao.UserDaoService;
import ru.ssau.loanofferservice.jpa.entity.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
@Transactional
@RunWith(SpringRunner.class)
@SpringBootTest(classes = LoanOfferApplication.class)
class AuthenticationServiceTest {

    @Autowired
    private AuthenticationService authService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserDaoService userDaoService;

    @Test
    void login() {
        User user = User.builder()
                .username("user-auth")
                .email("user-mail@n.com")
                .encryptPassword(passwordEncoder.encode("user-auth"))
                .role(Role.USER)
                .build();
        user = userDaoService.save(user);

        LoginDto dto = LoginDto.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .password("user-auth")
                .build();

        ResponseEntity<?> response = authService.login(dto);
        LoginDto login = (LoginDto) response.getBody();

        assertNotNull(login);
        assertEquals(login.getUsername(), dto.getUsername());
    }

    @Test
    void register() {
        UserDto userDto = UserDto.builder().build();
        ResponseEntity<?> entity = authService.register(userDto);
        ApiResponse response = (ApiResponse) entity.getBody();
        assertNotNull(response);
        assertNotNull(response.getErrorMessage());

        userDto.setUsername("user-auth");
        entity = authService.register(userDto);
        response = (ApiResponse) entity.getBody();
        assertNotNull(response);
        assertNotNull(response.getErrorMessage());

        userDto.setPassword("user-auth");
        entity = authService.register(userDto);
        response = (ApiResponse) entity.getBody();
        assertNotNull(response);
        assertNotNull(response.getErrorMessage());

        userDto.setEmail("user-mail@n.com");

        User user = User.builder()
                .username("user-auth")
                .email("user-mail@n.com")
                .encryptPassword(passwordEncoder.encode("user-auth"))
                .role(Role.USER)
                .build();
        user = userDaoService.save(user);
        entity = authService.register(userDto);
        response = (ApiResponse) entity.getBody();
        assertNotNull(response);
        assertNotNull(response.getErrorMessage());


        userDto.setUsername("user-auth1");
        userDto.setEmail("user-mail1@n.com");
        entity = authService.register(userDto);
        response = (ApiResponse) entity.getBody();
        assertNotNull(response);
        List<UserDto> list = (List<UserDto>) response.getContent();
        assertNotNull(list);
        assertEquals(1, list.size());

    }
}