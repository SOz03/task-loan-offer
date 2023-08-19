package ru.ssau.loanofferservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.ssau.loanofferservice.dto.CreditDto;
import ru.ssau.loanofferservice.security.config.principal.UserDetailsImpl;
import ru.ssau.loanofferservice.service.impl.CreditService;

import java.util.UUID;

import static ru.ssau.loanofferservice.dto.enums.ApiPaths.API_CREDITS;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = API_CREDITS)
@CrossOrigin(origins = "*", maxAge = 3600)
@PreAuthorize("authentication.isAuthenticated()")
public class CreditController {

    private final CreditService creditService;

    @GetMapping
    public ResponseEntity<?> select(Authentication authentication) {
        UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();
        log.info("Request for select credits accepted");
        return ResponseEntity.ok().body(creditService.select(principal));
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> get(@PathVariable UUID id, Authentication authentication) {
        UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();
        log.info("Request for get credit accepted");
        return ResponseEntity.ok().body(creditService.get(id));
    }

    @PostMapping()
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> create(@RequestBody CreditDto dto, Authentication authentication) {
        UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();
        log.info("Request for create credit accepted");
        return ResponseEntity.ok().body(creditService.create(dto, principal));
    }

    @PutMapping("/{updatedEntityId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> update(@PathVariable UUID updatedEntityId, @RequestBody CreditDto updateDto,
                                    Authentication authentication) {
        UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();
        log.info("Request for update credit accepted");
        return ResponseEntity.ok().body(creditService.update(updatedEntityId, updateDto, principal));
    }

    @DeleteMapping(path = "/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> delete(@PathVariable UUID id,
                                    Authentication authentication) {
        UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();
        log.info("Request for delete credit accepted");
        creditService.delete(id, principal.getId());
        return ResponseEntity.ok().build();
    }
}
