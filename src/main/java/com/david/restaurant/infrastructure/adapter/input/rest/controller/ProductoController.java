package com.david.restaurant.infrastructure.adapter.input.rest.controller;

import com.david.restaurant.domain.model.Producto;
import com.david.restaurant.domain.port.input.ProductoServicePort;
import com.david.restaurant.infrastructure.adapter.input.rest.dto.productoDto.ProductoRequest;
import com.david.restaurant.infrastructure.adapter.input.rest.dto.productoDto.ProductoResponse;
import com.david.restaurant.infrastructure.adapter.input.rest.mapper.ProductoRestMapper;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoServicePort productoServicePort;
    private final ProductoRestMapper productoRestMapper; 

    @PostMapping
    public ResponseEntity<ProductoResponse> save(@RequestBody ProductoRequest request) {
        Producto productoDomain = productoRestMapper.toDomain(request);
        Producto productoCreado = productoServicePort.save(productoDomain);
        return new ResponseEntity<>(productoRestMapper.toResponse(productoCreado), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ProductoResponse>> findAll() {
        List<ProductoResponse> productos = productoServicePort.findAll()
                .stream()
                .map(productoRestMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponse> findById(@PathVariable Long id) {
        Producto producto = productoServicePort.findById(id);
        return ResponseEntity.ok(productoRestMapper.toResponse(producto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponse> update(@PathVariable Long id, @RequestBody ProductoRequest request) {
        Producto productoUpdate = productoRestMapper.toDomain(request);
        Producto productoActualizado = productoServicePort.update(id, productoUpdate);
        return ResponseEntity.ok(productoRestMapper.toResponse(productoActualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        productoServicePort.deleteById(id);
        return ResponseEntity.noContent().build();
    }


}