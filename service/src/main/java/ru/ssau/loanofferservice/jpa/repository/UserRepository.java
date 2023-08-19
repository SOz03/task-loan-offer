package ru.ssau.loanofferservice.jpa.repository;

import org.springframework.stereotype.Repository;
import ru.ssau.loanofferservice.common.dao.repository.AbstractRepository;
import ru.ssau.loanofferservice.dto.enums.Role;
import ru.ssau.loanofferservice.jpa.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends AbstractRepository<User> {

    Optional<User> findUserByUsername(String username);

    Optional<User> findByUsernameOrEmail(String username, String email);

    List<User> findAllByRole(Role role);
}
