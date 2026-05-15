package com.david.restaurant.infrastructure.adapter.output.persistence.orden;

import com.david.restaurant.infrastructure.adapter.output.persistence.ingrediente.IngredienteEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "orden_item_ingredientes")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrdenItemIngredienteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private OrdenItemEntity ordenItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingrediente_id", nullable = false)
    private IngredienteEntity ingrediente;

    private Double cantidad;
    private Double precioUnitario; 
}
