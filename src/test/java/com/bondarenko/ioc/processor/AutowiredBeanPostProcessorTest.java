package com.bondarenko.ioc.processor;

import com.bondarenko.ioc.exception.NoUniqueBeanOfTypeException;
import com.bondarenko.ioc.testclasses.processor.MessageService;
import com.bondarenko.ioc.testclasses.processor.TestBean;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class AutowiredBeanPostProcessorTest {

    private AutowiredBeanPostProcessor beanPostProcessor;

    @Test
    @DisplayName("should post process before initialization successfully")
    void shouldPostProcessBeforeInitializationSuccessfully() {

        Map<Class<?>, List<Object>> groupedBeansByClass = new HashMap<>();
        List<Object> objects = new ArrayList<>();
        objects.add(new MessageService());
        groupedBeansByClass.put(MessageService.class, objects);

        beanPostProcessor = new AutowiredBeanPostProcessor(groupedBeansByClass);

        TestBean beanValue = new TestBean();

        Object actualTestBean = beanPostProcessor.postProcessBeforeInitialization("testBean", beanValue);

        assertNotNull(actualTestBean);
        assertEquals(TestBean.class, actualTestBean.getClass());
        assertNotNull(((TestBean) actualTestBean).getMessageService());
        assertEquals(995, (((TestBean) actualTestBean).getMessageService()).getPort());
        assertEquals("POP3", (((TestBean) actualTestBean).getMessageService()).getProtocol());
    }

    @Test
    @DisplayName("should throw NoUniqueBeanOfTypeException when multiple beans of a type are present")
    void shouldThrowNoUniqueBeanOfTypeExceptionWhenMultipleBeansPresent() {

        Map<Class<?>, List<Object>> groupedBeansByClass = new HashMap<>();
        List<Object> objects = new ArrayList<>();
        objects.add(new MessageService());
        objects.add(new MessageService());
        groupedBeansByClass.put(MessageService.class, objects);

        beanPostProcessor = new AutowiredBeanPostProcessor(groupedBeansByClass);

        TestBean beanValue = new TestBean();

        assertThrows(NoUniqueBeanOfTypeException.class, () ->
                beanPostProcessor.postProcessBeforeInitialization("testBean", beanValue));
    }
}