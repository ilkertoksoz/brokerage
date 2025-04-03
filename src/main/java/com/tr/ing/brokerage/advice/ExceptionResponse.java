package com.tr.ing.brokerage.advice;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public final class ExceptionResponse {
    private final LocalDateTime date;
    private final String message;
}