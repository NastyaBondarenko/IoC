package com.bondarenko.ioc.testclasses.publisher.listener;


import com.bondarenko.ioc.annotation.Component;
import com.bondarenko.ioc.testclasses.publisher.event.OrderCancelledEvent;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Component
public class OrderCancelledListener {

    final List<OrderCancelledEvent> orderCancelledEvents = new ArrayList<>();

    public void listen(OrderCancelledEvent orderCancelledEvent) {
        orderCancelledEvents.add(orderCancelledEvent);
    }
}