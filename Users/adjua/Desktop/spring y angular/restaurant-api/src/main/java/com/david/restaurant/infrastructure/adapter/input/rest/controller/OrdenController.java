package com.david.restaurant.infrastructure.adapter.input.rest.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.david.restaurant.domain.model.Orden;
import com.david.restaurant.domain.model.OrdenItem;
import com.david.restaurant.domain.model.Pago;
import com.david.restaurant.domain.port.input.OrdenServicePort;
import com.david.restaurant.infrastructure.adapter.input.rest.dto.ordenDto.OrdenEstadoRequest;
import com.david.restaurant.infrastructure.adapter.input.rest.dto.ordenDto.OrdenItemRequest;
import com.david.restaurant.infrastructure.adapter.input.rest.dto.ordenDto.OrdenRequest;
import com.david.restaurant.infrastructure.adapter.input.rest.dto.ordenDto.OrdenResponse;
import com.david.restaurant.infrastructure.adapter.input.rest.dto.ordenDto.PagoRequest;
import com.david.restaurant.infrastructure.adapter.input.rest.mapper.OrdenRestMapper;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/ordenes")
@RequiredArgsConstructor
public class OrdenController {

    private final OrdenServicePort ordenServicePort;
    private final OrdenRestMapper ordenRestMapper;

    @PostMapping
    public ResponseEntity<OrdenResponse> createOrden(@RequestBody OrdenRequest request) {
        Orden ordenDomain = ordenRestMapper.toDomain(request);
        Orden ordenCreada = ordenServicePort.createOrden(ordenDomain);
        return new ResponseEntity<>(ordenRestMapper.toResponse(ordenCreada), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<OrdenResponse>> findAll() {
        List<OrdenResponse> ordenes = ordenServicePort.findAll()
                .stream()
                .map(ordenRestMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ordenes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrdenResponse> findById(@PathVariable Long id) {
        Orden orden = ordenServicePort.findById(id);
        return ResponseEntity.ok(ordenRestMapper.toResponse(orden));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        ordenServicePort.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<OrdenResponse> updateEstado(@PathVariable Long id, @RequestBody OrdenEstadoRequest request) {
        Orden ordenActualizada = ordenServicePort.updateEstado(id, request.estado());
        return ResponseEntity.ok(ordenRestMapper.toResponse(ordenActualizada));
    }

    @PostMapping("/{id}/items")
    public ResponseEntity<OrdenResponse> addOrdenItem(@PathVariable Long id, @RequestBody OrdenItemRequest itemRequest) {
        OrdenItem nuevoItem = ordenRestMapper.toItemDomain(itemRequest);
        Orden ordenActualizada = ordenServicePort.addItemToOrden(id, nuevoItem);
        return ResponseEntity.ok(ordenRestMapper.toResponse(ordenActualizada));
    }

    @DeleteMapping("/{id}/items/{itemId}")
    public ResponseEntity<OrdenResponse> removeOrdenItem(@PathVariable Long id, @PathVariable Long itemId) {
        Orden ordenActualizada = ordenServicePort.removerItemDeOrden(id, itemId);
        return ResponseEntity.ok(ordenRestMapper.toResponse(ordenActualizada));
    }

    @PostMapping("/{id}/pagar")
    public ResponseEntity<OrdenResponse> pagarOrden(@PathVariable Long id, @RequestBody PagoRequest pagoRequest) {
        Pago nuevoPago = ordenRestMapper.toPagoDomain(pagoRequest); 
        Orden ordenActualizada = ordenServicePort.pagarOrden(id, nuevoPago); 
        return ResponseEntity.ok(ordenRestMapper.toResponse(ordenActualizada));
    }




}
