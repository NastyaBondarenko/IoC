package com.study.ioc.context.impl;

import com.study.entity.MailService;
import com.study.ioc.entity.Bean;
import com.study.ioc.processor.BeanFactoryPostProcessor;
import com.study.ioc.processor.CustomBeanFactoryPostProcessor;
import com.study.ioc.processor.CustomBeanPostProcessor;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class GenericApplicationContextITest {
    private GenericApplicationContext genericApplicationContext;

    @Before
    public void before() {
        genericApplicationContext = new GenericApplicationContext("context.xml");
    }

    @Test
    @DisplayName("Get Bean Names")
    public void testGetBeanNames() {
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
    public void testGetBeanById() {
        Map<String, Bean> beanMap = genericApplicationContext.getBeanMap();
        Object actualBean = genericApplicationContext.getBean("mailServicePOP");
        Object expectedBean = beanMap.get("mailServicePOP").getValue();

        assertNotNull(actualBean);
        assertEquals(expectedBean, actualBean);
        assertEquals(MailService.class, actualBean.getClass());
    }

    @Test
    @DisplayName("get Beans by Id And Class")
    public void testGetBeanByIdAndClass() {
        Map<String, Bean> beanMap = genericApplicationContext.getBeanMap();
        Object actualBean = genericApplicationContext.getBean("mailServicePOP", MailService.class);
        Object expectedBean = beanMap.get("mailServicePOP").getValue();

        assertNotNull(actualBean);
        assertEquals(expectedBean, actualBean);
        assertEquals(MailService.class, actualBean.getClass());
    }

    @Test
    @DisplayName("get Bean Post Processors")
    public void testGetBeanPostProcessors() {
        Map<String, Bean> beanPostProcessorsMap = genericApplicationContext.getBeanPostProcessorsMap();
        Bean actualBean = beanPostProcessorsMap.get("beanPostProcessor");

        assertNotNull(beanPostProcessorsMap);
        assertEquals(1, beanPostProcessorsMap.size());
        assertEquals("beanPostProcessor", actualBean.getId());
        assertEquals(CustomBeanPostProcessor.class, actualBean.getValue().getClass());
    }

    @Test
    @DisplayName("get Bean Factory Post Processors")
    public void testGetBeanFactoryPostProcessors() {
        List<BeanFactoryPostProcessor> beanFactoryPostProcessors = genericApplicationContext.getBeanFactoryPostProcessors();
        BeanFactoryPostProcessor beanFactoryPostProcessor = beanFactoryPostProcessors.get(0);

        assertNotNull(beanFactoryPostProcessors);
        assertEquals(1, beanFactoryPostProcessors.size());
        assertEquals(CustomBeanFactoryPostProcessor.class, beanFactoryPostProcessor.getClass());
    }
}