package com.bondarenko.ioc.context.impl;

import com.bondarenko.ioc.entity.Bean;
import com.bondarenko.ioc.entity.BeanDefinition;
import com.bondarenko.ioc.exception.BeanInstantiationException;
import com.bondarenko.ioc.exception.NoUniqueBeanOfTypeException;
import com.bondarenko.ioc.processor.BeanFactoryPostProcessor;
import com.bondarenko.ioc.processor.impl.CustomBeanFactoryPostProcessor;
import com.bondarenko.ioc.testclasses.context.impl.MailServiceImpl;
import com.bondarenko.ioc.testclasses.context.impl.UserServiceImpl;
import com.bondarenko.ioc.service.MessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class GenericApplicationContextTest {

    private GenericApplicationContext genericApplicationContext;

    @BeforeEach
    public void before() {
        genericApplicationContext = new GenericApplicationContext();
    }

    @Test
    void testCreateBeans() {
        Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();

        BeanDefinition beanDefinitionMailService =
                new BeanDefinition("mailServicePOP", "com.bondarenko.ioc.testclasses.context.impl.MailServiceImpl");

        beanDefinitionMap.put("mailServicePOP", beanDefinitionMailService);
        BeanDefinition beanDefinitionUserService =
                new BeanDefinition("userService", "com.bondarenko.ioc.testclasses.context.impl.UserServiceImpl");

        beanDefinitionMap.put("userService", beanDefinitionUserService);

        Map<String, Bean> beanMap = genericApplicationContext.createBeans(beanDefinitionMap);

        Bean actualMailBean = beanMap.get("mailServicePOP");
        assertNotNull(actualMailBean);
        assertEquals("mailServicePOP", actualMailBean.getId());
        assertEquals(MailServiceImpl.class, actualMailBean.getValue().getClass());

        Bean actualUserBean = beanMap.get("userService");
        assertNotNull(actualUserBean);
        assertEquals("userService", actualUserBean.getId());
        assertEquals(UserServiceImpl.class, actualUserBean.getValue().getClass());
    }


    @Test
    void testCreateBeansWithWrongClass() {
        Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();
        BeanDefinition errorBeanDefinition = new BeanDefinition("mailServicePOP", "com.study.entity.TestClass");
        beanDefinitionMap.put("mailServicePOP", errorBeanDefinition);

        assertThrows(BeanInstantiationException.class, () -> {
            genericApplicationContext.createBeans(beanDefinitionMap);
        });
    }

    @Test
    void testGetBeanById() {
        Map<String, Bean> beanMap = new HashMap<>();
        UserServiceImpl beanValue1 = new UserServiceImpl();
        UserServiceImpl beanValue2 = new UserServiceImpl();
        beanMap.put("bean1", new Bean("bean1", beanValue1));
        beanMap.put("bean2", new Bean("bean2", beanValue2));
        genericApplicationContext.setBeans(beanMap);
        UserServiceImpl actualBeanValue1 = (UserServiceImpl) genericApplicationContext.getBean("bean1");
        UserServiceImpl actualBeanValue2 = (UserServiceImpl) genericApplicationContext.getBean("bean2");
        assertNotNull(actualBeanValue1);
        assertNotNull(actualBeanValue2);
        assertEquals(beanValue1, actualBeanValue1);
        assertEquals(beanValue2, actualBeanValue2);
    }

    @Test
    void testGetBeanByClazz() {
        Map<String, Bean> beanMap = new HashMap<>();
        UserServiceImpl beanValue1 = new UserServiceImpl();
        MailServiceImpl beanValue2 = new MailServiceImpl();
        beanMap.put("bean1", new Bean("bean1", beanValue1));
        beanMap.put("bean2", new Bean("bean2", beanValue2));
        genericApplicationContext.setBeans(beanMap);
        UserServiceImpl actualBeanValue1 = genericApplicationContext.getBean(UserServiceImpl.class);
        MailServiceImpl actualBeanValue2 = genericApplicationContext.getBean(MailServiceImpl.class);
        assertNotNull(actualBeanValue1);
        assertNotNull(actualBeanValue2);
        assertEquals(beanValue1, actualBeanValue1);
        assertEquals(beanValue2, actualBeanValue2);
    }

    @Test
    void testGetBeanByClazzNoUniqueBean() {
        Map<String, Bean> beanMap = new HashMap<>();
        beanMap.put("bean1", new Bean("bean1", new UserServiceImpl()));
        beanMap.put("bean2", new Bean("bean2", new UserServiceImpl()));
        genericApplicationContext.setBeans(beanMap);

        assertThrows(NoUniqueBeanOfTypeException.class, () -> {
            genericApplicationContext.getBean(UserServiceImpl.class);
        });
    }

    @Test
    void testGetBeanByIdAndClazz() {
        Map<String, Bean> beanMap = new HashMap<>();
        UserServiceImpl beanValue1 = new UserServiceImpl();
        UserServiceImpl beanValue2 = new UserServiceImpl();
        beanMap.put("bean1", new Bean("bean1", beanValue1));
        beanMap.put("bean2", new Bean("bean2", beanValue2));
        genericApplicationContext.setBeans(beanMap);
        UserServiceImpl actualBeanValue1 = genericApplicationContext.getBean("bean1", UserServiceImpl.class);
        UserServiceImpl actualBeanValue2 = genericApplicationContext.getBean("bean2", UserServiceImpl.class);
        assertNotNull(actualBeanValue1);
        assertNotNull(actualBeanValue2);
        assertEquals(beanValue1, actualBeanValue1);
        assertEquals(beanValue2, actualBeanValue2);
    }


    @Test
    void testGetBeanByIdAndClazzNoSuchBean() {
        Map<String, Bean> beanMap = new HashMap<>();
        UserServiceImpl beanValue = new UserServiceImpl();
        beanMap.put("bean1", new Bean("bean1", beanValue));
        genericApplicationContext.setBeans(beanMap);
        genericApplicationContext.getBean(UserServiceImpl.class);
    }

    @Test
    void getBeanNames() {
        Map<String, Bean> beanMap = new HashMap<>();
        beanMap.put("bean3", new Bean("bean3", new UserServiceImpl()));
        beanMap.put("bean4", new Bean("bean4", new UserServiceImpl()));
        beanMap.put("bean5", new Bean("bean5", new UserServiceImpl()));
        genericApplicationContext.setBeans(beanMap);
        List<String> actualBeansNames = genericApplicationContext.getBeanNames();
        List<String> expectedBeansNames = Arrays.asList("bean3", "bean4", "bean5");
        assertTrue(actualBeansNames.containsAll(expectedBeansNames));
        assertTrue(expectedBeansNames.containsAll(actualBeansNames));
    }

    @Test
    void testInjectValueDependencies() {
        Map<String, Bean> beanMap = new HashMap<>();
        Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();

        MailServiceImpl mailServicePOP = new MailServiceImpl();
        beanMap.put("mailServicePOP", new Bean("mailServicePOP", mailServicePOP));
        MailServiceImpl mailServiceIMAP = new MailServiceImpl();
        beanMap.put("mailServiceIMAP", new Bean("mailServiceIMAP", mailServiceIMAP));

        //  setPort(110) and setProtocol("POP3") via valueDependencies
        BeanDefinition popServiceBeanDefinition = new BeanDefinition("mailServicePOP", "com.study.entity.MailService");
        Map<String, String> popServiceValueDependencies = new HashMap<>();
        popServiceValueDependencies.put("port", "110");
        popServiceValueDependencies.put("protocol", "POP3");
        popServiceBeanDefinition.setValueDependencies(popServiceValueDependencies);
        beanDefinitionMap.put("mailServicePOP", popServiceBeanDefinition);

        //  setPort(143) and setProtocol("IMAP") via valueDependencies
        BeanDefinition imapServiceBeanDefinition = new BeanDefinition("mailServiceIMAP", "com.study.entity.MailService");
        Map<String, String> imapServiceValueDependencies = new HashMap<>();
        imapServiceValueDependencies.put("port", "143");
        imapServiceValueDependencies.put("protocol", "IMAP");
        imapServiceBeanDefinition.setValueDependencies(imapServiceValueDependencies);
        beanDefinitionMap.put("mailServiceIMAP", imapServiceBeanDefinition);

        genericApplicationContext.injectValueDependencies(beanDefinitionMap, beanMap);
        assertEquals(110, mailServicePOP.getPort());
        assertEquals("POP3", mailServicePOP.getProtocol());
        assertEquals(143, mailServiceIMAP.getPort());
        assertEquals("IMAP", mailServiceIMAP.getProtocol());
    }

    @Test
    void testInjectRefDependencies() {
        Map<String, Bean> beanMap = new HashMap<>();
        Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();

        MailServiceImpl mailServicePOP = new MailServiceImpl();
        mailServicePOP.setPort(110);
        mailServicePOP.setProtocol("POP3");
        beanMap.put("mailServicePOP", new Bean("mailServicePOP", mailServicePOP));

        UserServiceImpl userService = new UserServiceImpl();
        beanMap.put("userService", new Bean("userService", userService));

        //  setMailService(mailServicePOP) via refDependencies
        BeanDefinition userServiceBeanDefinition = new BeanDefinition("userService", "com.study.entity.DefaultUserService");
        Map<String, String> userServiceRefDependencies = new HashMap<>();
        userServiceRefDependencies.put("mailService", "mailServicePOP");
        userServiceBeanDefinition.setRefDependencies(userServiceRefDependencies);
        beanDefinitionMap.put("userService", userServiceBeanDefinition);

        genericApplicationContext.injectRefDependencies(beanDefinitionMap, beanMap);
        assertNotNull(userService.getMailService());
        assertEquals(110, ((MailServiceImpl) userService.getMailService()).getPort());
        assertEquals("POP3", ((MailServiceImpl) userService.getMailService()).getProtocol());
    }

    @Test
    void testInjectValue() throws ReflectiveOperationException {
        MailServiceImpl mailService = new MailServiceImpl();
        Method setPortMethod = MailServiceImpl.class.getDeclaredMethod("setPort", Integer.TYPE);
        genericApplicationContext.injectValue(mailService, setPortMethod, "465");
        int actualPort = mailService.getPort();
        assertEquals(465, actualPort);
    }

    @Test
    @DisplayName("test Create BeanPostProcessors")
    void testCreateBeanPostProcessors() {
        Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();
        BeanDefinition beanDefinitionFactoryPostProcessor =
                new BeanDefinition("beanFactoryPostProcessor", "com.bondarenko.ioc.processor.impl.CustomBeanFactoryPostProcessor");
        BeanDefinition beanDefinitionUserService =
                new BeanDefinition("userService", "com.bondarenko.ioc.testclasses.context.impl.UserServiceImpl");
        beanDefinitionMap.put("userService", beanDefinitionUserService);
        beanDefinitionMap.put("beanFactoryPostProcessor", beanDefinitionFactoryPostProcessor);
        BeanDefinition beanDefinitionPostProcessor =
                new BeanDefinition("beanPostProcessor", "com.bondarenko.ioc.processor.impl.CustomBeanPostProcessor");
        beanDefinitionMap.put("beanPostProcessor", beanDefinitionPostProcessor);

        genericApplicationContext.createBeanPostProcessors(beanDefinitionMap);
        Map<String, Bean> beanPostProcessors = genericApplicationContext.getBeanPostProcessorsMap();
        List<BeanFactoryPostProcessor> beanFactoryPostProcessors = genericApplicationContext.getBeanFactoryPostProcessors();

        assertNotNull(beanPostProcessors);
        assertNotNull(beanFactoryPostProcessors);
        assertEquals(CustomBeanFactoryPostProcessor.class, beanFactoryPostProcessors.get(0).getClass());
    }

    @Test
    @DisplayName("process BeanDefinitions")
    void processBeanDefinitions() {
        Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();
        BeanDefinition beanDefinitionMailService
                = new BeanDefinition("mailServicePOP", "com.bondarenko.ioc.testclasses.context.impl.MailServiceImpl");
        beanDefinitionMap.put("mailServicePOP", beanDefinitionMailService);

        BeanDefinition beanDefinitionUserService = new BeanDefinition("userService", "com.bondarenko.ioc.testclasses.context.impl.UserServiceImpl");
        beanDefinitionMap.put("userService", beanDefinitionUserService);

        BeanDefinition beanDefinitionFactoryPostProcessor =
                new BeanDefinition("beanFactoryPostProcessor", "com.bondarenko.ioc.processor.impl.CustomBeanFactoryPostProcessor");
        beanDefinitionMap.put("beanFactoryPostProcessor", beanDefinitionFactoryPostProcessor);

        genericApplicationContext.createBeanPostProcessors(beanDefinitionMap);
        genericApplicationContext.processBeanDefinitions(beanDefinitionMap);
        Map<String, Bean> beanMap = genericApplicationContext.createBeans(beanDefinitionMap);
        genericApplicationContext.injectValueDependencies(beanDefinitionMap, beanMap);

        MailServiceImpl mailService = (MailServiceImpl) beanMap.get("mailServicePOP").getValue();
        assertEquals(4500, mailService.getPort());
    }

    @Test
    @DisplayName("test Process Beans Before Initialization")
    void testProcessBeansBeforeInitialization() {
        Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();

        BeanDefinition beanDefinitionMessageService =
                new BeanDefinition("messageService", "com.bondarenko.ioc.service.MessageService");

        beanDefinitionMap.put("messageService", beanDefinitionMessageService);

        BeanDefinition beanDefinitionMailService =
                new BeanDefinition("mailServiceIMAP", "com.bondarenko.ioc.testclasses.reader.MailService");

        beanDefinitionMap.put("mailServiceIMAP", beanDefinitionMailService);

        BeanDefinition beanDefinitionPostProcessor =
                new BeanDefinition("beanPostProcessor", "com.bondarenko.ioc.processor.impl.CustomBeanPostProcessor");

        beanDefinitionMap.put("beanPostProcessor", beanDefinitionPostProcessor);

        Map<String, Bean> beanMap = genericApplicationContext.createBeans(beanDefinitionMap);
        genericApplicationContext.createBeanPostProcessors(beanDefinitionMap);
        genericApplicationContext.processBeansBeforeInitialization(beanMap);

        Bean actualMessageService = beanMap.get("messageService");
        MessageService messageService = (MessageService) actualMessageService.getValue();
        assertEquals(5000, messageService.getPort());
        assertEquals("POP", messageService.getProtocol());
    }

    @Test
    @DisplayName("test Initialize Beans")
    void testInitializeBeans() {
        Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();
        BeanDefinition beanDefinitionMailService =
                new BeanDefinition("mailServicePOP", "com.bondarenko.ioc.testclasses.reader.MailService");
        beanDefinitionMap.put("mailServiceIMAP", beanDefinitionMailService);
        BeanDefinition beanDefinitionBeanPostProcessor =
                new BeanDefinition("beanPostProcessor", "com.bondarenko.ioc.processor.impl.CustomBeanPostProcessor");
        beanDefinitionMap.put("beanPostProcessor", beanDefinitionBeanPostProcessor);

        Map<String, Bean> beanMap = genericApplicationContext.createBeans(beanDefinitionMap);
        MailServiceImpl mailServicePOP = new MailServiceImpl();
        mailServicePOP.setPort(110);
        mailServicePOP.setProtocol("POP3");
        beanMap.put("mailServicePOP", new Bean("mailServicePOP", mailServicePOP));

        genericApplicationContext.createBeanPostProcessors(beanDefinitionMap);
        genericApplicationContext.processBeansBeforeInitialization(beanMap);
        genericApplicationContext.initializeBeans(beanMap);

        assertEquals(4467, mailServicePOP.getPort());
        assertEquals("IMAP", mailServicePOP.getProtocol());
    }

    @Test
    @DisplayName("test Process Beans After Initialization")
    void testProcessBeansAfterInitialization() {
        Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();

        BeanDefinition beanDefinitionMessageService =
                new BeanDefinition("messageService", "com.bondarenko.ioc.service.MessageService");
        beanDefinitionMap.put("messageService", beanDefinitionMessageService);
        BeanDefinition beanDefinitionMailService =
                new BeanDefinition("mailServiceIMAP", "com.bondarenko.ioc.testclasses.context.impl.MailServiceImpl");
        beanDefinitionMap.put("mailServiceIMAP", beanDefinitionMailService);
        BeanDefinition beanDefinitionPostProcessor =
                new BeanDefinition("beanPostProcessor", "com.bondarenko.ioc.processor.impl.CustomBeanPostProcessor");
        beanDefinitionMap.put("beanPostProcessor", beanDefinitionPostProcessor);


        Map<String, Bean> beanMap = genericApplicationContext.createBeans(beanDefinitionMap);
        genericApplicationContext.createBeanPostProcessors(beanDefinitionMap);
        genericApplicationContext.processBeansBeforeInitialization(beanMap);
        genericApplicationContext.initializeBeans(beanMap);
        genericApplicationContext.processBeansAfterInitialization(beanMap);

        Bean actualMessageService = beanMap.get("messageService");
        MessageService messageService = (MessageService) actualMessageService.getValue();
        assertEquals(6000, messageService.getPort());
        assertEquals("POP3", messageService.getProtocol());
    }
}