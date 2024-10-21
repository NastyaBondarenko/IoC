package com.bondarenko.ioc.publisher;

import com.bondarenko.ioc.context.impl.AnnotationBasedApplicationContext;
import com.bondarenko.ioc.testclasses.publisher.event.CustomerEvent;
import com.bondarenko.ioc.testclasses.publisher.event.OrderCompletedEvent;
import com.bondarenko.ioc.testclasses.publisher.listener.CustomerListener;
import com.bondarenko.ioc.testclasses.publisher.listener.OrderListener;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DefaultApplicationEventPublisherITest {

    private final String[] SCAN_PACKAGE_LISTENER = {"com.bondarenko.ioc.testclasses.publisher.listener"};
    private final String[] SCAN_PACKAGES_CASE_SECOND = {"com.bondarenko.ioc.testclasses.publisher.listener.caseSecond"};
    private final String[] SCAN_PACKAGES_CASE_THIRD = {"com.bondarenko.ioc.testclasses.publisher.listener.caseThird"};
    private final String[] SCAN_PACKAGES_CASE_FOURTH = {"com.bondarenko.ioc.testclasses.publisher.listener.caseFourth"};
    private final String[] SCAN_PACKAGES_CASE_FIFTH = {"com.bondarenko.ioc.testclasses.publisher.listener.caseFifth"};
    private AnnotationBasedApplicationContext context;

    @Test
    @DisplayName("should publish event once successfully")
    void shouldPublishEventOnceSuccessfully() {
        AnnotationBasedApplicationContext context = new AnnotationBasedApplicationContext(SCAN_PACKAGE_LISTENER);

        OrderListener orderListener = context.getBean("OrderListener", OrderListener.class);
        assertTrue(orderListener.getOrderCompletedEvents().isEmpty());

        context.getEventPublisher().publishEvent(new OrderCompletedEvent());

        assertEquals(1, orderListener.getOrderCompletedEvents().size());
    }

    @Test
    @DisplayName("should publish event inside listenersSuccessfully")
    void shouldPublishEventInsideListenerSuccessfully() {
        AnnotationBasedApplicationContext context = new AnnotationBasedApplicationContext(SCAN_PACKAGE_LISTENER);

        OrderListener orderListener = context.getBean("OrderListener", OrderListener.class);
        CustomerListener customerListener = context.getBean("CustomerListener", CustomerListener.class);
        assertTrue(orderListener.getOrderCompletedEvents().isEmpty());
        assertTrue(customerListener.getCustomerEvents().isEmpty());

        context.getEventPublisher().publishEvent(new CustomerEvent());

        assertEquals(1, orderListener.getOrderCompletedEvents().size());
    }
//
//    @Test
//    @DisplayName("should publish different events in two listeners successfully")
//    void shouldPublishDifferentEventsInTwoListenersSuccessfully() {
//        context = new AnnotationBasedApplicationContext(SCAN_PACKAGES_CASE_THIRD);
//        publisher = new DefaultApplicationEventPublisher(context.getEventHandlersMap(), context);
//
//        EventListenerThird eventListenerThird = (EventListenerThird) context.getBean("EventListenerThird");
//
//        assertTrue(eventListenerThird.getEvents().isEmpty());
//
//        publisher.publishEvent(new EventFirst());
//        publisher.publishEvent(new EventSecond());
//
//        assertEquals(2, eventListenerThird.getEvents().size());
//    }
//
//    @Test
//    @DisplayName("should not publish event when method without annotation listener ")
//    void shouldNotPublishEventWhenMethodWithoutAnnotationListener() {
//        context = new AnnotationBasedApplicationContext(SCAN_PACKAGES_CASE_FOURTH);
//        publisher = new DefaultApplicationEventPublisher(context.getEventHandlersMap(), context);
//
//        EventListenerFourth eventListenerFourth = (EventListenerFourth) context.getBean("EventListenerFourth");
//
//        assertTrue(eventListenerFourth.getEvents().isEmpty());
//
//        publisher.publishEvent(new EventFirst());
//
//        assertTrue(eventListenerFourth.getEvents().isEmpty());
//    }
//
//    @Test
//    @DisplayName("should not publish event when method without annotation listener ")
//    void shouldPublishEventWhenMethodWithoutAnnotationListener() {
//        context = new AnnotationBasedApplicationContext(SCAN_PACKAGES_CASE_FOURTH);
//        publisher = new DefaultApplicationEventPublisher(context.getEventHandlersMap(), context);
//
//        EventListenerFourth eventListenerFourth = (EventListenerFourth) context.getBean("EventListenerFourth");
//
//        assertTrue(eventListenerFourth.getEvents().isEmpty());
//
//        publisher.publishEvent(new EventFirst());
//
//        assertTrue(eventListenerFourth.getEvents().isEmpty());
//    }
//
//    @Test
//    @DisplayName("should publish the same event in two listeners successfully")
//    void shouldPublishTheSameEventInDifferentListenersSuccessfully() {
//        context = new AnnotationBasedApplicationContext(SCAN_PACKAGES_CASE_FIFTH);
//        publisher = new DefaultApplicationEventPublisher(context.getEventHandlersMap(), context);
//
//        EventListenerFifth eventListenerFifth = (EventListenerFifth) context.getBean("EventListenerFifth");
//        EventListenerSixth eventListenerSixth = (EventListenerSixth) context.getBean("EventListenerSixth");
//
//        assertTrue(eventListenerFifth.getEvents().isEmpty());
//        assertTrue(eventListenerSixth.getEvents().isEmpty());
//
//        publisher.publishEvent(new EventFirst());
//
//        assertEquals(1, eventListenerSixth.getEvents().size());
//        assertEquals(1, eventListenerFifth.getEvents().size());
//    }
}