package ru.ssau.loanofferservice.jpa.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.ssau.loanofferservice.common.service.AbstractServiceDao;
import ru.ssau.loanofferservice.dto.enums.Role;
import ru.ssau.loanofferservice.jpa.entity.User;
import ru.ssau.loanofferservice.jpa.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
public class UserDaoService extends AbstractServiceDao<User, UserRepository> {

    public UserDaoService(UserRepository repository) {
        super(repository);
    }

    public User findByUsername(String username) {
        return repository.findUserByUsername(username).orElse(null);
    }

    public User findByUsernameOrEmail(String username, String email) {
        return repository.findByUsernameOrEmail(username, email).orElse(null);
    }

    public List<User> getAll(){
        return repository.findAllByRole(Role.USER);
    }

}
