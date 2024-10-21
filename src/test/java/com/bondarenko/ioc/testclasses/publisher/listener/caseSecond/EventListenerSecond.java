package com.bondarenko.ioc.testclasses.publisher.listener.caseSecond;

import com.bondarenko.ioc.annotation.Component;
import com.bondarenko.ioc.annotation.EventListener;
import com.bondarenko.ioc.testclasses.publisher.event.EventSecond;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Component
public class EventListenerSecond {

    final List<EventSecond> events = new ArrayList<>();

    @EventListener(EventSecond.class)
    public void listen(EventSecond event) {
        events.add(event);
    }

    @EventListener(EventSecond.class)
    public void listenSecond(EventSecond event) {
        events.add(event);
    }
}