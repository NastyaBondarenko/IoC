package com.bondarenko.ioc.publisher;

import com.bondarenko.ioc.context.impl.AnnotationBasedApplicationContext;
import com.bondarenko.ioc.exception.ListenerNotFoundException;
import com.bondarenko.ioc.testclasses.publisher.event.CustomerEvent;
import com.bondarenko.ioc.testclasses.publisher.event.CustomerRegisteredEvent;
import com.bondarenko.ioc.testclasses.publisher.event.PaymentProcessedEvent;
import com.bondarenko.ioc.testclasses.publisher.listener.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DefaultApplicationEventPublisherITest {

    private final String[] SCAN_PACKAGE_LISTENER = {"com.bondarenko.ioc.testclasses.publisher.listener"};

    @Test
    @DisplayName("should publish event once successfully")
    void shouldPublishEventOnceSuccessfully() {
        AnnotationBasedApplicationContext context = new AnnotationBasedApplicationContext(SCAN_PACKAGE_LISTENER);

        PaymentListener paymentListener = context.getBean("PaymentListener", PaymentListener.class);
        assertTrue(paymentListener.getPaymentProcessedEvents().isEmpty());

        context.getEventPublisher().publishEvent(new PaymentProcessedEvent());

        assertEquals(1, paymentListener.getPaymentProcessedEvents().size());
    }

    @Test
    @DisplayName("should publish event twice in listeners successfully")
    void shouldPublishEventTwiceInListenersSuccessfully() {
        AnnotationBasedApplicationContext context = new AnnotationBasedApplicationContext(SCAN_PACKAGE_LISTENER);

        CustomerRegisteredListener customerRegisteredEvent = context.getBean("CustomerRegisteredListener", CustomerRegisteredListener.class);
        assertTrue(customerRegisteredEvent.getCustomerRegisteredEvents().isEmpty());

        context.getEventPublisher().publishEvent(new CustomerRegisteredEvent());

        assertEquals(2, customerRegisteredEvent.getCustomerRegisteredEvents().size());
    }

    @Test
    @DisplayName("should publish event inside listener successfully")
    void shouldPublishEventInsideListenerSuccessfully() {
        AnnotationBasedApplicationContext context = new AnnotationBasedApplicationContext(SCAN_PACKAGE_LISTENER);

        OrderCompletedListener orderCompletedListener = context.getBean("OrderCompletedListener", OrderCompletedListener.class);
        CustomerListener customerListener = context.getBean("CustomerListener", CustomerListener.class);
        assertTrue(orderCompletedListener.getOrderCompletedEvents().isEmpty());
        assertTrue(customerListener.getCustomerEvents().isEmpty());

        context.getEventPublisher().publishEvent(new CustomerEvent());

        assertEquals(1, customerListener.getCustomerEvents().size());
        assertEquals(1, orderCompletedListener.getOrderCompletedEvents().size());
    }

    @Test
    @DisplayName("should throw exception when publish event without annotation listener")
    void shouldThrowExceptionWhenPublishEventWithoutAnnotationListener() {
        AnnotationBasedApplicationContext context = new AnnotationBasedApplicationContext(SCAN_PACKAGE_LISTENER);

        OrderCancelledListener orderCancelledListener = context.getBean("OrderCancelledListener", OrderCancelledListener.class);
        assertTrue(orderCancelledListener.getOrderCancelledEvents().isEmpty());

        assertThrows(ListenerNotFoundException.class, () -> {
            context.getEventPublisher().publishEvent(new OrderCancelledListener());
        });
    }
}