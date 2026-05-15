package com.david.restaurant.domain.port.output;

import com.david.restaurant.domain.model.OrdenEvento;

public interface OrdenEventPort {
    void publicar(OrdenEvento evento);
}
