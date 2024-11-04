package com.bondarenko.ioc.testclasses.publisher.listener;


import com.bondarenko.ioc.annotation.Component;
import com.bondarenko.ioc.annotation.EventListener;
import com.bondarenko.ioc.testclasses.publisher.event.PaymentProcessedEvent;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Component
public class PaymentListener {

    final List<PaymentProcessedEvent> paymentProcessedEvents = new ArrayList<>();

    @EventListener(PaymentProcessedEvent.class)
    public void listen(PaymentProcessedEvent paymentProcessedEvent) {
        paymentProcessedEvents.add(paymentProcessedEvent);
    }
}