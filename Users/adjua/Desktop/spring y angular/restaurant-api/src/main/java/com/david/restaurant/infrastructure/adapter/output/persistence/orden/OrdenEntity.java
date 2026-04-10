package com.david.restaurant.infrastructure.adapter.output.persistence.orden;

import com.david.restaurant.domain.model.EstadoOrden;
import com.david.restaurant.domain.model.TipoOrden;
import com.david.restaurant.infrastructure.adapter.output.persistence.mesa.MesaEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "ordenes")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrdenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TipoOrden tipoOrden;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mesa_id", nullable = true) 
    private MesaEntity mesa;

    private String nombreCliente;
    private String telefonoCliente;
    private String direccionEntrega;

    private LocalDateTime fechaCreacion;

    @Enumerated(EnumType.STRING)
    private EstadoOrden estado;
    
    private Double totalMonto;

    @OneToMany(mappedBy = "orden", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrdenItemEntity> items;

    @OneToMany(mappedBy = "orden", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PagoEntity> pagos;
}
