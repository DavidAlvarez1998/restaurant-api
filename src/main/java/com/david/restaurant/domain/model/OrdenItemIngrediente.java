package com.david.restaurant.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrdenItemIngrediente {
    private Long id;
    private Ingrediente ingrediente; 
    private Double cantidad;         
    private Double precioUnitario;   
}
