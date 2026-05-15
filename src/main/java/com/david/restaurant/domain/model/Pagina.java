package com.david.restaurant.domain.model;

import java.util.List;

public record Pagina<T>(
    List<T> contenido,
    boolean hasNext
) {}
