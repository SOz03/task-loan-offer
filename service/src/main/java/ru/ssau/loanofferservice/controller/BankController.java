package ru.ssau.loanofferservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.ssau.loanofferservice.dto.BankDto;
import ru.ssau.loanofferservice.dto.response.ApiResponse;
import ru.ssau.loanofferservice.security.config.principal.UserDetailsImpl;
import ru.ssau.loanofferservice.service.impl.BankService;

import java.util.List;
import java.util.UUID;

import static ru.ssau.loanofferservice.dto.enums.ApiPaths.API_BANKS;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = API_BANKS)
@CrossOrigin(origins = "*", maxAge = 3600)
@PreAuthorize("authentication.isAuthenticated()")
public class BankController {

    private final BankService bankService;

    @GetMapping
    public ResponseEntity<?> select(Authentication authentication) {
        UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();
        log.info("Request for select banks accepted");
        List<BankDto> response = bankService.select(principal);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> get(@PathVariable UUID id, Authentication authentication) {
        UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();
        log.info("Request for get bank accepted");

        ApiResponse content = bankService.get(id);

        return ResponseEntity.ok()
                .body(content);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> create(@RequestBody BankDto dto,
                                    Authentication authentication) {
        UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();
        log.info("Request for create bank accepted");

        ApiResponse content = bankService.create(dto, principal);

        return ResponseEntity.ok()
                .body(content);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody BankDto updateDto,
                                    Authentication authentication) {
        UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();
        log.info("Request for update bank accepted");

        ApiResponse content = bankService.update(id, updateDto, principal);

        return ResponseEntity.ok()
                .body(content);
    }

    @DeleteMapping(path = "/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> delete(@PathVariable UUID id,
                                    Authentication authentication) {
        UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();
        log.info("Request for delete bank accepted");

        bankService.delete(id, principal.getId());

        return ResponseEntity.ok().build();
    }

}
