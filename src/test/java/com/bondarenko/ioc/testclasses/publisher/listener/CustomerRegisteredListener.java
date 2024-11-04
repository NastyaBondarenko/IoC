package com.bondarenko.ioc.testclasses.publisher.listener;


import com.bondarenko.ioc.annotation.Component;
import com.bondarenko.ioc.annotation.EventListener;
import com.bondarenko.ioc.testclasses.publisher.event.CustomerRegisteredEvent;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Component
public class CustomerRegisteredListener {

    final List<CustomerRegisteredEvent> customerRegisteredEvents = new ArrayList<>();

    @EventListener(CustomerRegisteredEvent.class)
    public void listenFirst(CustomerRegisteredEvent customerRegisteredEvent) {
        customerRegisteredEvents.add(customerRegisteredEvent);
    }

    @EventListener(CustomerRegisteredEvent.class)
    public void listenSecond(CustomerRegisteredEvent customerRegisteredEvent) {
        customerRegisteredEvents.add(customerRegisteredEvent);
    }
}