package ru.ssau.loanofferservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ssau.loanofferservice.dto.LoginDto;
import ru.ssau.loanofferservice.dto.UserDto;
import ru.ssau.loanofferservice.dto.enums.Role;
import ru.ssau.loanofferservice.dto.response.Errors;
import ru.ssau.loanofferservice.dto.response.Response;
import ru.ssau.loanofferservice.security.config.principal.UserDetailsImpl;
import ru.ssau.loanofferservice.security.service.JwtTokenProvider;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService implements Response {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    public ResponseEntity<?> login(final LoginDto request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtTokenProvider.generate(authentication);

        UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();
        Role role = jwtTokenProvider.getRole(principal);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(new LoginDto(principal.getUsername(),
                        role,
                        principal.getEmail(),
                        principal.getFullname(),
                        principal.getPhone(),
                        principal.getCity(),
                        jwt));
    }

    @Transactional
    public ResponseEntity<?> register(UserDto newUser) {
        if (StringUtils.isBlank(newUser.getUsername())) {
            return ResponseEntity.ok().body(errorResponse(Errors.USERNAME_IS_NULL.name()));
        }
        if (StringUtils.isBlank(newUser.getPassword())) {
            return ResponseEntity.ok().body(errorResponse(Errors.PASSWORD_IS_NULL.name()));
        }
        if (StringUtils.isBlank(newUser.getEmail())) {
            return ResponseEntity.ok().body(errorResponse(Errors.EMAIL_IS_NULL.name()));
        }
        if (userService.findByUsernameOrMail(newUser) != null) {
            return ResponseEntity.ok().body(errorResponse(Errors.INVALID_LOGIN_OR_EMAIL.name()));
        }
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
//        newUser.setPassword(newUser.getPassword());
        UserDto userDto = userService.create(newUser);

        return ResponseEntity.ok().body(singleResponse(userDto));
    }

}
