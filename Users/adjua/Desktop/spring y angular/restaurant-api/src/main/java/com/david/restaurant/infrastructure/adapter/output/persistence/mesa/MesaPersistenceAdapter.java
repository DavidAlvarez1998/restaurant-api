package com.david.restaurant.infrastructure.adapter.output.persistence.mesa;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.david.restaurant.domain.model.Mesa;
import com.david.restaurant.domain.port.output.MesaRepositoryPort;

import lombok.RequiredArgsConstructor;


@Component
@RequiredArgsConstructor
public class MesaPersistenceAdapter implements MesaRepositoryPort {
    
    private final MesaRepository mesaRepository;
    private final MesaMapper mesaMapper;

    @Override
    public Mesa save(Mesa mesa) {
        MesaEntity mesaEntity = mesaMapper.toEntity(mesa);
        MesaEntity saveEntity = mesaRepository.save(mesaEntity);
        return mesaMapper.toDomain(saveEntity);
    }

    @Override
    public List<Mesa> findAll(){
        return mesaRepository.findAll().stream().map(mesaMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<Mesa> findById(Long id){
        return mesaRepository.findById(id).map(mesaMapper::toDomain); 
    }

    @Override 
    public void deleteById(Long id){
        mesaRepository.deleteById(id);
    }

    @Override
    public Boolean existsByNumero(String numero){
        return mesaRepository.existsByNumero(numero);
    } 

    @Override
    public Boolean existsById(Long id) {
        return mesaRepository.existsById(id);
    }


    
    
}
