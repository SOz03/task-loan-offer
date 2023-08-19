package ru.ssau.loanofferservice.dto.response;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public interface Response {

    default <D> ApiResponse singleResponse(D dtoContent) {
        return ApiResponse.builder()
                .totalElements(1)
                .content(Collections.singletonList(dtoContent))
                .build();
    }

    default <D> ApiResponse response(List<D> contents) {
        return ApiResponse.builder()
                .totalElements(contents.size())
                .content(contents)
                .build();
    }

    default <D> ApiResponse errorResponse(String errorMessage) {
        return ApiResponse.builder()
                .errorMessage(errorMessage)
                .content(new ArrayList<>())
                .build();
    }

}
