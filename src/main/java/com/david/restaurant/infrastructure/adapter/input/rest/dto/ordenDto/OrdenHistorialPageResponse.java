package com.david.restaurant.infrastructure.adapter.input.rest.dto.ordenDto;

import java.util.List;

public record OrdenHistorialPageResponse(
    List<OrdenResponse> content,
    int page,
    boolean hasNext
) {}
