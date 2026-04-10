package com.david.restaurant.infrastructure.adapter.input.rest.mapper;

import com.david.restaurant.domain.model.Producto;
import com.david.restaurant.domain.model.TipoProducto;
import com.david.restaurant.infrastructure.adapter.input.rest.dto.productoDto.ProductoRequest;
import com.david.restaurant.infrastructure.adapter.input.rest.dto.productoDto.ProductoResponse;
import org.springframework.stereotype.Component;

@Component
public class ProductoRestMapper {
    
    public Producto toDomain(ProductoRequest request) {
        if (request == null) return null;
        
        Producto producto = new Producto();
        producto.setNombre(request.nombre());
        producto.setDescripcion(request.descripcion());
        producto.setPrecio(request.precio());
        
        if (request.tipo() != null) {
            producto.setTipo(TipoProducto.valueOf(request.tipo()));
        }
        return producto;
    }

    public ProductoResponse toResponse(Producto producto) {
        if (producto == null) return null;
        
        return new ProductoResponse(
            producto.getId(),
            producto.getNombre(),
            producto.getDescripcion(),
            producto.getPrecio(),
            producto.getTipo() != null ? producto.getTipo().name() : null
        );
    }
}
