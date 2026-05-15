package com.david.restaurant.domain.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrdenItem {
    private Long id;
    private Producto producto;       
    private Integer cantidad;        
    private Double precioUnitario;   
    private String notas;            
    
    private List<OrdenItemIngrediente> ingredientes; 
}
