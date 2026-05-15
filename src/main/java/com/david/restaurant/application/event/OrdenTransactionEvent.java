package com.david.restaurant.application.event;

import com.david.restaurant.domain.model.OrdenEvento;
import org.springframework.context.ApplicationEvent;

public class OrdenTransactionEvent extends ApplicationEvent {

    private final OrdenEvento ordenEvento;

    public OrdenTransactionEvent(Object source, OrdenEvento ordenEvento) {
        super(source);
        this.ordenEvento = ordenEvento;
    }

    public OrdenEvento getOrdenEvento() {
        return ordenEvento;
    }
}
