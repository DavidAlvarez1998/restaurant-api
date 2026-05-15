package com.david.restaurant.infrastructure.adapter.input.rest.dto.ordenDto;

import java.util.List;

public record OrdenDividirRequest(List<ItemDividir> items) {
    public record ItemDividir(Long itemId, int cantidad) {}
}
