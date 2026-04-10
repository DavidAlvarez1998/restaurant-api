package com.david.restaurant.infrastructure.adapter.output.persistence.orden;

import com.david.restaurant.infrastructure.adapter.output.persistence.producto.ProductoEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "orden_items")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrdenItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orden_id", nullable = false)
    private OrdenEntity orden;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private ProductoEntity producto;

    private Integer cantidad;
    private Double precioUnitario;
    private String notas;

    @OneToMany(mappedBy = "ordenItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrdenItemIngredienteEntity> ingredientes;
}
