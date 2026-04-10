package com.david.restaurant.domain.port.input;

import java.util.List;
import com.david.restaurant.domain.model.Mesa;

public interface MesaServicePort {
    Mesa saveMesa(Mesa mesa);
    List<Mesa> findAll();
    Mesa findById(Long id);
    void deleteById(Long id);
    Mesa updateMesa(Long id, Mesa mesaUpdate);
}