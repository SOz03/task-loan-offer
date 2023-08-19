package ru.ssau.loanofferservice.service.impl;

import ru.ssau.loanofferservice.dto.response.ApiResponse;
import ru.ssau.loanofferservice.dto.response.Response;
import ru.ssau.loanofferservice.security.config.principal.UserDetailsImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public interface ApiService<D> extends Response {

    List<D> select(UserDetailsImpl principal);

    ApiResponse get(UUID uuid);

    ApiResponse create(D d, UserDetailsImpl principal);

    ApiResponse update(UUID id, D dto, UserDetailsImpl principal);

    boolean delete(UUID uuid, UUID userId);

}
