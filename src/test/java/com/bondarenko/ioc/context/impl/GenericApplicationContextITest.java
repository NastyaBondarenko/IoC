package com.bondarenko.ioc.context.impl;

import com.bondarenko.ioc.entity.Bean;
import com.bondarenko.ioc.processor.BeanFactoryPostProcessor;
import com.bondarenko.ioc.processor.impl.CustomBeanFactoryPostProcessor;
import com.bondarenko.ioc.processor.impl.CustomBeanPostProcessor;
import com.bondarenko.ioc.testclasses.context.impl.MailServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class GenericApplicationContextITest {
    private GenericApplicationContext genericApplicationContext;

    @BeforeEach
    public void before() {
        genericApplicationContext = new GenericApplicationContext("context.xml");
    }

    @Test
    @DisplayName("Get Bean Names")
    void testGetBeanNames() {
        List<String> actualBeanNames = genericApplicationContext.getBeanNames();
        List<String> expectedBeanNames = new ArrayList<>();

        expectedBeanNames.add("beanFactoryPostProcessor");
        expectedBeanNames.add("mailServicePOP");
        expectedBeanNames.add("beanPostProcessor");
        expectedBeanNames.add("userServiceImap");
        expectedBeanNames.add("messageService");
        expectedBeanNames.add("mailServiceIMAP");
        expectedBeanNames.add("userService");

        assertEquals(expectedBeanNames, actualBeanNames);
        assertEquals(expectedBeanNames.size(), actualBeanNames.size());
    }

    @Test
    @DisplayName("get Beans by Id")
    void testGetBeanById() {
        Map<String, Bean> beanMap = genericApplicationContext.getBeanMap();
        Object actualBean = genericApplicationContext.getBean("mailServicePOP");
        Object expectedBean = beanMap.get("mailServicePOP").getValue();

        assertNotNull(actualBean);
        assertEquals(expectedBean, actualBean);
        assertEquals(MailServiceImpl.class, actualBean.getClass());
    }

    @Test
    @DisplayName("get Beans by Id And Class")
    void testGetBeanByIdAndClass() {
        Map<String, Bean> beanMap = genericApplicationContext.getBeanMap();
        Object actualBean = genericApplicationContext.getBean("mailServicePOP", MailServiceImpl.class);
        Object expectedBean = beanMap.get("mailServicePOP").getValue();

        assertNotNull(actualBean);
        assertEquals(expectedBean, actualBean);
        assertEquals(MailServiceImpl.class, actualBean.getClass());
    }

    @Test
    @DisplayName("get Bean Post Processors")
    void testGetBeanPostProcessors() {
        Map<String, Bean> beanPostProcessorsMap = genericApplicationContext.getBeanPostProcessorsMap();
        Bean actualBean = beanPostProcessorsMap.get("beanPostProcessor");

        assertNotNull(beanPostProcessorsMap);
        assertEquals(1, beanPostProcessorsMap.size());
        assertEquals("beanPostProcessor", actualBean.getId());
        assertEquals(CustomBeanPostProcessor.class, actualBean.getValue().getClass());
    }

    @Test
    @DisplayName("get Bean Factory Post Processors")
    void testGetBeanFactoryPostProcessors() {
        List<BeanFactoryPostProcessor> beanFactoryPostProcessors = genericApplicationContext.getBeanFactoryPostProcessors();
        BeanFactoryPostProcessor beanFactoryPostProcessor = beanFactoryPostProcessors.get(0);

        assertNotNull(beanFactoryPostProcessors);
        assertEquals(1, beanFactoryPostProcessors.size());
        assertEquals(CustomBeanFactoryPostProcessor.class, beanFactoryPostProcessor.getClass());
    }
}