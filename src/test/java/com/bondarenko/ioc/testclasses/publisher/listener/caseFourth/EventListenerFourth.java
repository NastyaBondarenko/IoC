package com.bondarenko.ioc.testclasses.publisher.listener.caseFourth;

import com.bondarenko.ioc.testclasses.publisher.event.EventFirst;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class EventListenerFourth {

    final List<EventFirst> events = new ArrayList<>();

    public void listen(EventFirst event) {
        events.add(event);
    }
}