package com.david.restaurant.infrastructure.adapter.input.rest.advice;

import java.time.LocalDateTime;

public record ErrorResponse(
    String codigo,
    String mensaje,
    LocalDateTime timestamp
) {
    
}