package com.david.restaurant.domain.model;

public enum EstadoOrden {
    ABIERTA,
    EN_PREPARACION,
    LISTA,
    EN_CAMINO,
    ENTREGADA,
    PAGADA,   // legacy — no longer set by new logic
    CANCELADA
}
