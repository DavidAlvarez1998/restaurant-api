package com.david.restaurant.infrastructure.adapter.input.rest.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.david.restaurant.domain.model.Mesa;
import com.david.restaurant.domain.port.input.MesaServicePort;
import com.david.restaurant.infrastructure.adapter.input.rest.dto.MesaDto.MesaRequest;
import com.david.restaurant.infrastructure.adapter.input.rest.dto.MesaDto.MesaResponse;
import com.david.restaurant.infrastructure.adapter.input.rest.mapper.MesaRestMapper;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/mesas")
@RequiredArgsConstructor
public class MesaController {

    private final MesaServicePort mesaServicePort;
    private final MesaRestMapper mesaRestMapper;

    @PostMapping
    public ResponseEntity<MesaResponse> save(@RequestBody MesaRequest request) {
        Mesa mesaDomain = mesaRestMapper.toDomain(request);
        Mesa mesaCreada = mesaServicePort.saveMesa(mesaDomain);
        return new ResponseEntity<>(mesaRestMapper.toResponse(mesaCreada), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<MesaResponse>> findAll() {
        List<MesaResponse> mesas = mesaServicePort.findAll()
                .stream()
                .map(mesaRestMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(mesas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MesaResponse> findById(@PathVariable Long id) {
        Mesa mesa = mesaServicePort.findById(id);
        return ResponseEntity.ok(mesaRestMapper.toResponse(mesa));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MesaResponse> update(@PathVariable Long id, @RequestBody MesaRequest request) {
        Mesa mesaUpdate = mesaRestMapper.toDomain(request);
        Mesa mesaActualizada = mesaServicePort.updateMesa(id, mesaUpdate);
        return ResponseEntity.ok(mesaRestMapper.toResponse(mesaActualizada));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        mesaServicePort.deleteById(id);
        return ResponseEntity.noContent().build();
    }


}