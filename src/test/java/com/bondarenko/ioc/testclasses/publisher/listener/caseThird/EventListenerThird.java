package com.bondarenko.ioc.testclasses.publisher.listener.caseThird;


import com.bondarenko.ioc.annotation.EventListener;
import com.bondarenko.ioc.testclasses.publisher.event.EventFirst;
import com.bondarenko.ioc.testclasses.publisher.event.EventSecond;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class EventListenerThird {

    final List<Object> events = new ArrayList<>();

    @EventListener(EventFirst.class)
    public void listen(EventFirst event) {
        events.add(event);
    }

    @EventListener(EventSecond.class)
    public void listenSecond(EventSecond event) {
        events.add(event);
    }
}