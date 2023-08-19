package ru.ssau.loanofferservice.service;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.ssau.loanofferservice.dto.UserDto;
import ru.ssau.loanofferservice.dto.enums.Role;
import ru.ssau.loanofferservice.jpa.dao.UserDaoService;
import ru.ssau.loanofferservice.jpa.entity.User;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final Gson gson;
    private final ModelMapper modelMapper;
    private final UserDaoService userDaoService;

    @Transactional(propagation = Propagation.REQUIRED)
    public UserDto create(UserDto dto) {
        log.debug("Create a new user={}", gson.toJson(dto));

        User user = modelMapper.map(dto, User.class);
        user.setEncryptPassword(dto.getPassword());
        user.setRole(Role.USER);

        log.debug("Saving a new user");
        user = userDaoService.save(user);

        log.debug("User creation complete");
        return modelMapper.map(user, UserDto.class);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public UserDto findByUsernameOrMail(UserDto userDto) {
        log.debug("Find user by username={} or email={}", userDto.getUsername(), userDto.getEmail());
        User user = userDaoService.findByUsernameOrEmail(userDto.getUsername(), userDto.getEmail());
        return (user == null) ? null : modelMapper.map(user, UserDto.class);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<UserDto> getAll() {
        return userDaoService.getAll().stream()
                .map(el -> modelMapper.map(el, UserDto.class))
                .collect(Collectors.toList());
    }

}
