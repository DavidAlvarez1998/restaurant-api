package com.david.restaurant.infrastructure.adapter.output.persistence.orden;

import com.david.restaurant.domain.model.EstadoOrden;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdenRepository extends JpaRepository<OrdenEntity, Long> {

    @Query("SELECT o FROM OrdenEntity o WHERE o.estado IN :estados OR (o.estado = :entregada AND o.pagada = true) ORDER BY o.fechaModificacion DESC NULLS LAST")
    Slice<OrdenEntity> findHistorial(
        @Param("estados") List<EstadoOrden> estados,
        @Param("entregada") EstadoOrden entregada,
        Pageable pageable
    );
}
