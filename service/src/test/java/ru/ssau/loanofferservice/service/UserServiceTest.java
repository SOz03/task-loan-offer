package ru.ssau.loanofferservice.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import ru.ssau.loanofferservice.LoanOfferApplication;
import ru.ssau.loanofferservice.dto.UserDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@Transactional
@RunWith(SpringRunner.class)
@SpringBootTest(classes = LoanOfferApplication.class)
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    void allMethodsInService() {
        UserDto userDto = UserDto.builder()
                .username("user-test-create")
                .email("user-test-create")
                .password("user")
                .city("Moskow")
                .build();
        UserDto created = userService.create(userDto);
        assertEquals(userDto.getUsername(), created.getUsername());

        UserDto response = userService.findByUsernameOrMail(userDto);
        assertEquals(userDto.getCity(), response.getCity());

        response = userService.findByUsernameOrMail(UserDto.builder().build());
        assertNull(response);

        List<UserDto> list = userService.getAll();
        UserDto dtoNeed = list.stream()
                .filter(dto -> dto.getUsername().equals(userDto.getUsername()))
                .findFirst().orElse(null);
        assertNotNull(dtoNeed);
    }

}