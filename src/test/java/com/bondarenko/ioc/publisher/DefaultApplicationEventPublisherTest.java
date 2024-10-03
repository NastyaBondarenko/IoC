package com.bondarenko.ioc.publisher;

import com.bondarenko.ioc.context.GenericApplicationContext;
import com.bondarenko.ioc.publisher.impl.DefaultApplicationEventPublisher;
import com.bondarenko.ioc.testclasses.publisher.CustomEvent;
import com.bondarenko.ioc.testclasses.publisher.CustomListener;
import com.bondarenko.ioc.testclasses.publisher.ListenerFirst;
import com.bondarenko.ioc.testclasses.publisher.ListenerSecond;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultApplicationEventPublisherTest {
    @Mock
    private GenericApplicationContext applicationContext;

    @InjectMocks
    private DefaultApplicationEventPublisher eventPublisher;

    private Map<Class<?>, List<Method>> eventHandlersMap;

    @BeforeEach
    void setUp() throws NoSuchMethodException {
        eventHandlersMap = new HashMap<>();

        Method listenerFirst = ListenerFirst.class.getMethod("handleEvent", Object.class);
        Method listenerSecond = ListenerSecond.class.getMethod("handleEvent", Object.class);

        List<Method> listeners = new ArrayList<>();
        listeners.add(listenerFirst);
        listeners.add(listenerSecond);
        eventHandlersMap.put(Object.class, listeners);

        eventPublisher = new DefaultApplicationEventPublisher(eventHandlersMap, applicationContext);
    }

    @Test
    @DisplayName("should publish event successfully to two listeners")
    void shouldPublishEventSuccessfullyToTwoListeners() {
        ListenerFirst listenerFirst = mock(ListenerFirst.class);
        ListenerSecond listenerSecond = mock(ListenerSecond.class);

        when(applicationContext.getBean(ListenerFirst.class)).thenReturn(listenerFirst);
        when(applicationContext.getBean(ListenerSecond.class)).thenReturn(listenerSecond);

        Object testEvent = new Object();
        eventPublisher.publishEvent(testEvent);

        verify(listenerFirst, times(1)).handleEvent(testEvent);
        verify(listenerSecond, times(1)).handleEvent(testEvent);
    }

    @Test
    @DisplayName("should publish event once to listener")
    void shouldPublishEventOnceToListener() {
        ListenerFirst listenerFirst = mock(ListenerFirst.class);

        when(applicationContext.getBean(ListenerFirst.class)).thenReturn(listenerFirst);

        Object testEvent = new Object();
        eventPublisher.publishEvent(testEvent);

        verify(listenerFirst, times(1)).handleEvent(testEvent);
    }

    @Test
    @DisplayName("should not call any listeners for unknown event type")
    void shouldNotCallAnyListenersForUnknownEventType() {
        Object unknownEvent = new Object();
        eventPublisher.publishEvent(unknownEvent);

        verify(applicationContext, never()).getBean(any(String.class));
    }

    @Test
    @DisplayName("should publish event of different types")
    void shouldPublishDifferentEventTypes() throws NoSuchMethodException {
        Method customListenerMethod = CustomListener.class.getMethod("handleCustomEvent", CustomEvent.class);
        eventHandlersMap.put(CustomEvent.class, List.of(customListenerMethod));

        CustomListener customListenerInstance = mock(CustomListener.class);
        when(applicationContext.getBean(CustomListener.class)).thenReturn(customListenerInstance);

        CustomEvent customEvent = new CustomEvent();
        eventPublisher.publishEvent(customEvent);

        verify(customListenerInstance, times(1)).handleCustomEvent(customEvent);
    }

    @Test
    @DisplayName("should publish event twice to one listener")
    void shouldPublishEventTwiceToOneListener() {
        ListenerFirst listenerFirst = mock(ListenerFirst.class);

        when(applicationContext.getBean(ListenerFirst.class)).thenReturn(listenerFirst);

        Object testEvent = new Object();

        eventPublisher.publishEvent(testEvent);
        eventPublisher.publishEvent(testEvent);

        verify(listenerFirst, times(2)).handleEvent(testEvent);
    }
}