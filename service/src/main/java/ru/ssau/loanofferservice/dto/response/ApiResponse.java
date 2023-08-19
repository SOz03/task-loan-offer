package ru.ssau.loanofferservice.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Builder
public class ApiResponse implements Serializable {

    private static final long serialVersionUID = -4197888029746286277L;

    private String errorMessage;
    private int totalElements;

    private List<?> content;
}
