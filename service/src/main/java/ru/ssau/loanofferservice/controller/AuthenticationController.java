package ru.ssau.loanofferservice.controller;

import com.google.gson.Gson;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ssau.loanofferservice.dto.LoginDto;
import ru.ssau.loanofferservice.dto.UserDto;
import ru.ssau.loanofferservice.service.AuthenticationService;

import static org.springframework.http.MediaType.*;
import static ru.ssau.loanofferservice.dto.enums.ApiPaths.*;

@Slf4j
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping(path = API_AUTHENTICATION)
@RequiredArgsConstructor
public class AuthenticationController {

    private final Gson gson;
    private final AuthenticationService authenticationService;

    @PostMapping(path = LOGIN, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> authenticationUser(@RequestBody LoginDto request) {
        log.info("Authentication user {}", request.getUsername());
        return ResponseEntity.ok(authenticationService.login(request));
    }

    @PostMapping(path = REGISTRATION, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> registration(@RequestBody UserDto request) {
        log.info("New registration user={}", gson.toJson(request));

        return ResponseEntity.ok(authenticationService.register(request));
    }

}
