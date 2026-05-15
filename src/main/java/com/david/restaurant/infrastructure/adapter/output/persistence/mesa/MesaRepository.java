package com.david.restaurant.infrastructure.adapter.output.persistence.mesa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MesaRepository extends JpaRepository<MesaEntity, Long> {
    Boolean existsByNumero(String numero);    
}
