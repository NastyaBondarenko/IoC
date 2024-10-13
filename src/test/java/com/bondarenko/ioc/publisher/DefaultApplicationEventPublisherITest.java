package com.bondarenko.ioc.publisher;

import com.bondarenko.ioc.context.impl.AnnotationBasedApplicationContext;
import com.bondarenko.ioc.publisher.impl.DefaultApplicationEventPublisher;
import com.bondarenko.ioc.testclasses.publisher.event.EventFirst;
import com.bondarenko.ioc.testclasses.publisher.event.EventSecond;
import com.bondarenko.ioc.testclasses.publisher.listener.caseFifth.EventListenerFifth;
import com.bondarenko.ioc.testclasses.publisher.listener.caseFifth.EventListenerSixth;
import com.bondarenko.ioc.testclasses.publisher.listener.caseFirst.EventListenerFirst;
import com.bondarenko.ioc.testclasses.publisher.listener.caseFourth.EventListenerFourth;
import com.bondarenko.ioc.testclasses.publisher.listener.caseSecond.EventListenerSecond;
import com.bondarenko.ioc.testclasses.publisher.listener.caseThird.EventListenerThird;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DefaultApplicationEventPublisherITest {

    private final String[] SCAN_PACKAGES_CASE_FIRST = {"com.bondarenko.ioc.testclasses.publisher.listener.caseFirst"};
    private final String[] SCAN_PACKAGES_CASE_SECOND = {"com.bondarenko.ioc.testclasses.publisher.listener.caseSecond"};
    private final String[] SCAN_PACKAGES_CASE_THIRD = {"com.bondarenko.ioc.testclasses.publisher.listener.caseThird"};
    private final String[] SCAN_PACKAGES_CASE_FOURTH = {"com.bondarenko.ioc.testclasses.publisher.listener.caseFourth"};
    private final String[] SCAN_PACKAGES_CASE_FIFTH = {"com.bondarenko.ioc.testclasses.publisher.listener.caseFifth"};
    private DefaultApplicationEventPublisher publisher;
    private AnnotationBasedApplicationContext context;

    @Test
    @DisplayName("should publish event once successfully")
    void shouldPublishEventOnceSuccessfully() {
        context = new AnnotationBasedApplicationContext(SCAN_PACKAGES_CASE_FIRST);
        publisher = new DefaultApplicationEventPublisher(context.getEventHandlersMap(), context);

        EventListenerFirst eventListenerFirst = (EventListenerFirst) context.getBean("EventListenerFirst");

        assertTrue(eventListenerFirst.getEvents().isEmpty());

        publisher.publishEvent(new EventFirst());

        assertEquals(1, eventListenerFirst.getEvents().size());
    }

    @Test
    @DisplayName("should publish the same event in two listeners successfully")
    void shouldPublishTheSameEventInTwoListenersSuccessfully() {
        context = new AnnotationBasedApplicationContext(SCAN_PACKAGES_CASE_SECOND);
        publisher = new DefaultApplicationEventPublisher(context.getEventHandlersMap(), context);

        EventListenerSecond eventListenerSecond = (EventListenerSecond) context.getBean("EventListenerSecond");

        assertTrue(eventListenerSecond.getEvents().isEmpty());

        publisher.publishEvent(new EventSecond());

        assertEquals(2, eventListenerSecond.getEvents().size());
    }

    @Test
    @DisplayName("should publish different events in two listeners successfully")
    void shouldPublishDifferentEventsInTwoListenersSuccessfully() {
        context = new AnnotationBasedApplicationContext(SCAN_PACKAGES_CASE_THIRD);
        publisher = new DefaultApplicationEventPublisher(context.getEventHandlersMap(), context);

        EventListenerThird eventListenerThird = (EventListenerThird) context.getBean("EventListenerThird");

        assertTrue(eventListenerThird.getEvents().isEmpty());

        publisher.publishEvent(new EventFirst());
        publisher.publishEvent(new EventSecond());

        assertEquals(2, eventListenerThird.getEvents().size());
    }

    @Test
    @DisplayName("should not publish event when method without annotation listener ")
    void shouldNotPublishEventWhenMethodWithoutAnnotationListener() {
        context = new AnnotationBasedApplicationContext(SCAN_PACKAGES_CASE_FOURTH);
        publisher = new DefaultApplicationEventPublisher(context.getEventHandlersMap(), context);

        EventListenerFourth eventListenerFourth = (EventListenerFourth) context.getBean("EventListenerFourth");

        assertTrue(eventListenerFourth.getEvents().isEmpty());

        publisher.publishEvent(new EventFirst());

        assertTrue(eventListenerFourth.getEvents().isEmpty());
    }

    @Test
    @DisplayName("should not publish event when method without annotation listener ")
    void shouldPublishEventWhenMethodWithoutAnnotationListener() {
        context = new AnnotationBasedApplicationContext(SCAN_PACKAGES_CASE_FOURTH);
        publisher = new DefaultApplicationEventPublisher(context.getEventHandlersMap(), context);

        EventListenerFourth eventListenerFourth = (EventListenerFourth) context.getBean("EventListenerFourth");

        assertTrue(eventListenerFourth.getEvents().isEmpty());

        publisher.publishEvent(new EventFirst());

        assertTrue(eventListenerFourth.getEvents().isEmpty());
    }

    @Test
    @DisplayName("should publish the same event in two listeners successfully")
    void shouldPublishTheSameEventInDifferentListenersSuccessfully() {
        context = new AnnotationBasedApplicationContext(SCAN_PACKAGES_CASE_FIFTH);
        publisher = new DefaultApplicationEventPublisher(context.getEventHandlersMap(), context);

        EventListenerFifth eventListenerFifth = (EventListenerFifth) context.getBean("EventListenerFifth");
        EventListenerSixth eventListenerSixth = (EventListenerSixth) context.getBean("EventListenerSixth");

        assertTrue(eventListenerFifth.getEvents().isEmpty());
        assertTrue(eventListenerSixth.getEvents().isEmpty());

        publisher.publishEvent(new EventFirst());

        assertEquals(1, eventListenerSixth.getEvents().size());
        assertEquals(1, eventListenerFifth.getEvents().size());
    }
}