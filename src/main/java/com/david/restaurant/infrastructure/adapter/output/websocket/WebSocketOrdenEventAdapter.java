package com.david.restaurant.infrastructure.adapter.output.websocket;

import com.david.restaurant.domain.model.OrdenEvento;
import com.david.restaurant.domain.port.output.OrdenEventPort;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WebSocketOrdenEventAdapter implements OrdenEventPort {

    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void publicar(OrdenEvento evento) {
        OrdenEventMessage message = new OrdenEventMessage(
            evento.tipo().name(),
            evento.orden() != null ? evento.orden().getId() : null,
            evento.orden() != null && evento.orden().getEstado() != null ? evento.orden().getEstado().name() : null,
            evento.orden() != null && evento.orden().isPagada()
        );
        messagingTemplate.convertAndSend("/topic/ordenes", message);
    }
}
