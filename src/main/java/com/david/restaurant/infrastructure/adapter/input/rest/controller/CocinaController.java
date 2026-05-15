package com.david.restaurant.infrastructure.adapter.input.rest.controller;

import com.david.restaurant.domain.port.input.OrdenServicePort;
import com.david.restaurant.infrastructure.adapter.input.rest.dto.ordenDto.OrdenCocinaResponse;
import com.david.restaurant.infrastructure.adapter.input.rest.mapper.OrdenRestMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cocina")
@RequiredArgsConstructor
public class CocinaController {

    private final OrdenServicePort ordenServicePort;
    private final OrdenRestMapper ordenRestMapper;

    @GetMapping("/pendientes")
    public ResponseEntity<List<OrdenCocinaResponse>> getOrdenesPendientes() {
        List<OrdenCocinaResponse> ordenes = ordenServicePort.findOrdenesPendientes()
            .stream()
            .map(ordenRestMapper::toCocinaResponse)
            .collect(Collectors.toList());
        return ResponseEntity.ok(ordenes);
    }

    @GetMapping("/finalizadas")
    public ResponseEntity<List<OrdenCocinaResponse>> getOrdenesFinalizadas() {
        List<OrdenCocinaResponse> ordenes = ordenServicePort.findOrdenesFinalizadas()
            .stream()
            .map(ordenRestMapper::toCocinaResponse)
            .collect(Collectors.toList());
        return ResponseEntity.ok(ordenes);
    }

}
