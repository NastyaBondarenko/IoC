package com.bondarenko.ioc.testclasses.publisher.listener.caseFourth;

import com.bondarenko.ioc.annotation.Component;
import com.bondarenko.ioc.testclasses.publisher.event.OrderCompletedEvent;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Component
public class EventListenerFourth {

    final List<OrderCompletedEvent> events = new ArrayList<>();

    public void listen(OrderCompletedEvent event) {
        events.add(event);
    }
}