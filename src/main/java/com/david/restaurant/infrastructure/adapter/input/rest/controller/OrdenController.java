package com.david.restaurant.infrastructure.adapter.input.rest.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.david.restaurant.domain.model.Orden;
import com.david.restaurant.domain.model.OrdenItem;
import com.david.restaurant.domain.model.Pagina;
import com.david.restaurant.domain.model.Pago;
import com.david.restaurant.domain.port.input.OrdenServicePort;
import com.david.restaurant.domain.model.OrdenDivision;
import com.david.restaurant.domain.model.ResultadoDivision;
import com.david.restaurant.domain.model.SepararItemResultado;
import com.david.restaurant.infrastructure.adapter.input.rest.dto.ordenDto.OrdenDividirRequest;
import com.david.restaurant.infrastructure.adapter.input.rest.dto.ordenDto.OrdenDividirResponse;
import com.david.restaurant.infrastructure.adapter.input.rest.dto.ordenDto.SepararItemResponse;
import com.david.restaurant.infrastructure.adapter.input.rest.dto.ordenDto.OrdenEstadoRequest;
import com.david.restaurant.infrastructure.adapter.input.rest.dto.ordenDto.OrdenItemRequest;
import com.david.restaurant.infrastructure.adapter.input.rest.dto.ordenDto.OrdenRequest;
import com.david.restaurant.infrastructure.adapter.input.rest.dto.ordenDto.OrdenResponse;
import com.david.restaurant.infrastructure.adapter.input.rest.dto.ordenDto.OrdenHistorialPageResponse;
import com.david.restaurant.infrastructure.adapter.input.rest.dto.ordenDto.OrdenUpdateRequest;
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

    @GetMapping("/historial")
    public ResponseEntity<OrdenHistorialPageResponse> getHistorial(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pagina<Orden> pagina = ordenServicePort.findHistorialPaginado(page, size);
        List<OrdenResponse> content = pagina.contenido().stream()
            .map(ordenRestMapper::toResponse)
            .collect(Collectors.toList());
        OrdenHistorialPageResponse response = new OrdenHistorialPageResponse(
            content,
            page,
            pagina.hasNext()
        );
        return ResponseEntity.ok(response);
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

    @PatchMapping("/{id}")
    public ResponseEntity<OrdenResponse> updateOrden(@PathVariable Long id, @RequestBody OrdenUpdateRequest request) {
        Orden datos = ordenRestMapper.toUpdateDomain(request);
        Orden ordenActualizada = ordenServicePort.updateOrden(id, datos);
        return ResponseEntity.ok(ordenRestMapper.toResponse(ordenActualizada));
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

    @PatchMapping("/{id}/items/{itemId}")
    public ResponseEntity<OrdenResponse> updateOrdenItem(
            @PathVariable Long id,
            @PathVariable Long itemId,
            @RequestBody OrdenItemRequest itemRequest) {
        OrdenItem itemActualizado = ordenRestMapper.toItemDomain(itemRequest);
        Orden ordenActualizada = ordenServicePort.updateOrdenItem(id, itemId, itemActualizado);
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

    @PostMapping("/{id}/items/{itemId}/separar")
    public ResponseEntity<SepararItemResponse> separarItem(
            @PathVariable Long id,
            @PathVariable Long itemId) {
        SepararItemResultado resultado = ordenServicePort.separarItem(id, itemId);
        return ResponseEntity.ok(new SepararItemResponse(
            ordenRestMapper.toResponse(resultado.orden()),
            resultado.nuevoItemId()
        ));
    }

    @PostMapping("/{id}/dividir")
    public ResponseEntity<OrdenDividirResponse> dividirOrden(
            @PathVariable Long id,
            @RequestBody OrdenDividirRequest request) {
        List<OrdenDivision> items = request.items().stream()
            .map(i -> new OrdenDivision(i.itemId(), i.cantidad()))
            .collect(Collectors.toList());
        ResultadoDivision resultado = ordenServicePort.dividirOrden(id, items);
        return ResponseEntity.ok(new OrdenDividirResponse(
            ordenRestMapper.toResponse(resultado.original()),
            ordenRestMapper.toResponse(resultado.nueva())
        ));
    }
}
