package com.bondarenko.ioc.testclasses.publisher.listener;


import com.bondarenko.ioc.annotation.Component;
import com.bondarenko.ioc.annotation.EventListener;
import com.bondarenko.ioc.testclasses.publisher.event.OrderCompletedEvent;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Component
public class OrderListener {

    final List<OrderCompletedEvent> orderCompletedEvents = new ArrayList<>();

    @EventListener(OrderCompletedEvent.class)
    public void listen(OrderCompletedEvent orderCompletedEvent) {
        orderCompletedEvents.add(orderCompletedEvent);
    }
}