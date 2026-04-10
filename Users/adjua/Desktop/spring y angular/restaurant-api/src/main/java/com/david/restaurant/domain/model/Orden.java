package com.david.restaurant.domain.model;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Orden {
    private Long id;
    
    private TipoOrden tipoOrden;       // EN_MESA, PARA_LLEVAR, DOMICILIO
    private Mesa mesa;                 
    
    // Datos del Cliente (Útil para domicilios o reservas)
    private String nombreCliente;      
    private String telefonoCliente;    
    private String direccionEntrega;   
    
    // Datos de Facturación / Estado
    private LocalDateTime fechaCreacion; 
    private EstadoOrden estado;
    private Double totalMonto;         
    
    private List<OrdenItem> items;
    
    // Los Pagos recibidos (Tickets)
    private List<Pago> pagos;
}
