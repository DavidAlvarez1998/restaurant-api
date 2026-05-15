package com.david.restaurant.infrastructure.adapter.output.websocket;

public record OrdenEventMessage(String tipo, Long ordenId, String estado, boolean pagada) {}
