package com.bondarenko.ioc.testclasses.publisher.listener.caseThird;


import com.bondarenko.ioc.annotation.Component;
import com.bondarenko.ioc.annotation.EventListener;
import com.bondarenko.ioc.testclasses.publisher.event.OrderCompletedEvent;
import com.bondarenko.ioc.testclasses.publisher.event.EventSecond;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Component
public class EventListenerThird {

    final List<Object> events = new ArrayList<>();

    @EventListener(OrderCompletedEvent.class)
    public void listen(OrderCompletedEvent event) {
        events.add(event);
    }

    @EventListener(EventSecond.class)
    public void listenSecond(EventSecond event) {
        events.add(event);
    }
}