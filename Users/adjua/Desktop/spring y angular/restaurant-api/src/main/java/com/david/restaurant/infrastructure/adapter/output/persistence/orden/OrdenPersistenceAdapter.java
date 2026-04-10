package com.david.restaurant.infrastructure.adapter.output.persistence.orden;

import com.david.restaurant.domain.model.Orden;
import com.david.restaurant.domain.port.output.OrdenRepositoryPort;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrdenPersistenceAdapter implements OrdenRepositoryPort {

    private final OrdenRepository ordenRepository;
    private final OrdenMapper ordenMapper;

    @Override
    public Orden save(Orden orden) {
        OrdenEntity entity = ordenMapper.toEntity(orden);
        OrdenEntity savedEntity = ordenRepository.save(entity);
        return ordenMapper.toDomain(savedEntity);
    }

    @Override
    public List<Orden> findAll() {
        return ordenRepository.findAll().stream()
                .map(ordenMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Orden> findById(Long id) {
        return ordenRepository.findById(id)
                .map(ordenMapper::toDomain);
    }

    @Override
    public void deleteById(Long id) {
        ordenRepository.deleteById(id);  
    }

    @Override
    public boolean existsById(Long id) {
        return ordenRepository.existsById(id);
    }

}
