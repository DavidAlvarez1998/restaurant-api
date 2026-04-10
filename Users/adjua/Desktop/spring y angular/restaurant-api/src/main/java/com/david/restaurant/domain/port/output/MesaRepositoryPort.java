package com.david.restaurant.domain.port.output;

import com.david.restaurant.domain.model.Mesa;
import java.util.List;
import java.util.Optional;

public interface MesaRepositoryPort {
    Mesa save(Mesa mesa);
    List<Mesa> findAll();
    Optional<Mesa> findById(Long id);
    void deleteById(Long id);
    Boolean existsByNumero(String numero);
    Boolean existsById(Long id);
}

