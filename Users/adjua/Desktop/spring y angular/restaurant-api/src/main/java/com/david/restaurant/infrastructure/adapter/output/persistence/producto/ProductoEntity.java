package com.david.restaurant.infrastructure.adapter.output.persistence.producto;

import com.david.restaurant.domain.model.TipoProducto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "productos")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String descripcion; 
    private Double precio;

    @Enumerated(EnumType.STRING) 
    private TipoProducto tipo;

}