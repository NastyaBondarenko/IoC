package com.study.ioc.context.impl;

import com.study.entity.DefaultUserService;
import com.study.entity.MailService;
import com.study.ioc.entity.Bean;
import com.study.ioc.entity.BeanDefinition;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class GenericApplicationContextITest {

    private GenericApplicationContext genericApplicationContext;

    @Before
    public void before() {
        genericApplicationContext = new GenericApplicationContext();
    }

    @Test
    public void testProcessBeans() {
        Map<String, Bean> beanMap = new HashMap<>();
        Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();

        MailService mailServicePOP = new MailService();
        mailServicePOP.setPort(110);
        mailServicePOP.setProtocol("POP3");
        beanMap.put("mailServicePOP", new Bean("mailServicePOP", mailServicePOP));

        DefaultUserService userService = new DefaultUserService();
        beanMap.put("userService", new Bean("userService", userService));
        BeanDefinition userServiceBeanDefinition =
                new BeanDefinition("userService", "com.study.entity.DefaultUserService");

        Map<String, String> userServiceRefDependencies = new HashMap<>();
        userServiceRefDependencies.put("mailService", "mailServicePOP");
        userServiceBeanDefinition.setRefDependencies(userServiceRefDependencies);
        beanDefinitionMap.put("userService", userServiceBeanDefinition);

        genericApplicationContext.injectRefDependencies(beanDefinitionMap, beanMap);

        assertEquals("mailServicePOP", userServiceBeanDefinition.getRefDependencies().get("mailService"));

        genericApplicationContext.modifyBeanDefinitions(beanDefinitionMap);

        assertEquals("newServiceIMAP", userServiceBeanDefinition.getRefDependencies().get("newService"));
        assertEquals(null, userServiceBeanDefinition.getRefDependencies().get("mailService"));

        genericApplicationContext.createBeans(beanDefinitionMap);

        assertEquals("userService", beanMap.get("userService").getId());
        assertEquals(userService, beanMap.get("userService").getValue());

        assertEquals(110, mailServicePOP.getPort());
        assertEquals("POP3", mailServicePOP.getProtocol());

        genericApplicationContext.processBeansBeforeInitialization();
        beanMap.containsValue("userService");
        beanMap.containsValue("updatedService");

        genericApplicationContext.initializeBeans(beanMap);

        assertEquals(4467, mailServicePOP.getPort());
        assertEquals("IMAP", mailServicePOP.getProtocol());

        genericApplicationContext.processBeansAfterInitialization();
        beanMap.containsValue("userService");
        beanMap.containsValue("newService");

    }
}