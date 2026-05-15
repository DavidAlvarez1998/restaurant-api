package com.david.restaurant.infrastructure.adapter.output.websocket;

import com.david.restaurant.application.event.OrdenTransactionEvent;
import com.david.restaurant.domain.port.output.OrdenEventPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class OrdenTransactionEventListener {

    private final OrdenEventPort ordenEventPort;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(OrdenTransactionEvent event) {
        ordenEventPort.publicar(event.getOrdenEvento());
    }
}
