package ru.ssau.loanofferservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.ssau.loanofferservice.dto.LoanOfferDto;
import ru.ssau.loanofferservice.dto.response.ApiResponse;
import ru.ssau.loanofferservice.security.config.principal.UserDetailsImpl;
import ru.ssau.loanofferservice.service.impl.LoanOfferService;

import java.util.List;
import java.util.UUID;

import static ru.ssau.loanofferservice.dto.enums.ApiPaths.API_LOAN_OFFER;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = API_LOAN_OFFER)
@CrossOrigin(origins = "*", maxAge = 3600)
@PreAuthorize("authentication.isAuthenticated()")
public class LoanOfferController {

    private final LoanOfferService service;

    @GetMapping
    public ResponseEntity<?> select(Authentication authentication) {
        UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();
        log.info("Request for select loan offer accepted");
        List<LoanOfferDto> response = service.select(principal);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> get(@PathVariable UUID id, Authentication authentication) {
        UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();
        log.info("Request for get loan offer accepted");

        ApiResponse content = service.get(id);

        return ResponseEntity.ok()
                .body(content);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> create(@RequestBody LoanOfferDto dto,
                                    Authentication authentication) {
        UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();
        log.info("Request for create loan offer accepted");

        ApiResponse content = service.create(dto, principal);

        return ResponseEntity.ok()
                .body(content);
    }

    @DeleteMapping(path = "/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> delete(@PathVariable UUID id,
                                    Authentication authentication) {
        UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();
        log.info("Request for delete loan offer accepted");

        service.delete(id, principal.getId());

        return ResponseEntity.ok().build();
    }

}
