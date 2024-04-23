package com.bondarenko.ioc.context.impl;


import com.bondarenko.ioc.entity.Bean;
import com.bondarenko.ioc.entity.BeanDefinition;
import com.bondarenko.ioc.exception.BeanInstantiationException;
import com.bondarenko.ioc.exception.NoSuchBeanDefinitionException;
import com.bondarenko.ioc.exception.NoUniqueBeanOfTypeException;
import com.bondarenko.ioc.processor.BeanFactoryPostProcessor;
import com.bondarenko.ioc.testclasses.processor.CustomBeanFactoryPostProcessor;
import com.bondarenko.ioc.testclasses.processor.MessageService;
import com.bondarenko.ioc.testclasses.reader.DefaultUserService;
import com.bondarenko.ioc.testclasses.reader.MailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


public class AnnotationBasedApplicationContextTest {

    private AnnotationBasedApplicationContext annotationBasedApplicationContext;


    @BeforeEach
    public void before() {
        annotationBasedApplicationContext = new AnnotationBasedApplicationContext("com.study.testclasses");

    }

    @Test
    @DisplayName("should create beans successfully")
    void shouldCreateBeansSuccessfully() {
        Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();
        BeanDefinition beanDefinitionMailService = new BeanDefinition("MailService", "com.bondarenko.ioc.testclasses.reader.MailService");
        beanDefinitionMap.put("MailService", beanDefinitionMailService);
        BeanDefinition beanDefinitionUserService = new BeanDefinition("DefaultUserService", "com.bondarenko.ioc.testclasses.reader.DefaultUserService");
        beanDefinitionMap.put("DefaultUserService", beanDefinitionUserService);

        Map<String, Bean> beanMap = annotationBasedApplicationContext.createBeans(beanDefinitionMap);

        Bean actualMailBean = beanMap.get("MailService");
        assertEquals("MailService", actualMailBean.getId());
        assertEquals(MailService.class, actualMailBean.getValue().getClass());

        Bean actualUserBean = beanMap.get("DefaultUserService");
        assertNotNull(actualUserBean);
        assertEquals("DefaultUserService", actualUserBean.getId());
        assertEquals(DefaultUserService.class, actualUserBean.getValue().getClass());
    }

    @Test
    @DisplayName("should create beans with wrong class successfully")
    void shouldCreateBeansWithWrongClassSuccessfully() {
        Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();
        BeanDefinition errorBeanDefinition = new BeanDefinition("mailServicePOP", "com.study.entity.TestClass");
        beanDefinitionMap.put("mailServicePOP", errorBeanDefinition);

        assertThrows(BeanInstantiationException.class, () -> {
            annotationBasedApplicationContext.createBeans(beanDefinitionMap);
        });
    }

//    @Test
//    @DisplayName("should get bean by id successfully")
//    void shouldGetBeanByIdSuccessfully() {
//        Map<String, Bean> beanMap = new HashMap<>();
//        DefaultUserService beanValue1 = new DefaultUserService();
//        DefaultUserService beanValue2 = new DefaultUserService();
//        beanMap.put("bean1", new Bean("bean1", beanValue1));
//        beanMap.put("bean2", new Bean("bean2", beanValue2));
//
//        annotationBasedApplicationContext.setBeans(beanMap);
//        DefaultUserService actualBeanValue1 = (DefaultUserService) annotationBasedApplicationContext.getBean("bean1");
//        DefaultUserService actualBeanValue2 = (DefaultUserService) annotationBasedApplicationContext.getBean("bean2");
//        assertNotNull(actualBeanValue1);
//        assertNotNull(actualBeanValue2);
//        assertEquals(beanValue1, actualBeanValue1);
//        assertEquals(beanValue2, actualBeanValue2);
//    }

    @Test
    @DisplayName("should get bean by clazz successfully")
    void shouldGetBeanByClazzSuccessfully() {
        Map<String, Bean> beanMap = new HashMap<>();
        DefaultUserService beanValue1 = new DefaultUserService();
        MailService beanValue2 = new MailService();
        beanMap.put("bean1", new Bean("bean1", beanValue1));
        beanMap.put("bean2", new Bean("bean2", beanValue2));
        annotationBasedApplicationContext.setBeans(beanMap);
        DefaultUserService actualBeanValue1 = annotationBasedApplicationContext.getBean(DefaultUserService.class);
        MailService actualBeanValue2 = annotationBasedApplicationContext.getBean(MailService.class);
        assertNotNull(actualBeanValue1);
        assertNotNull(actualBeanValue2);
        assertEquals(beanValue1, actualBeanValue1);
        assertEquals(beanValue2, actualBeanValue2);
    }

    @Test
    @DisplayName("should get bean by clazz no unique bean successfully")
    void shouldGetBeanByClazzNoUniqueBeanSuccessfully() {
        Map<String, Bean> beanMap = new HashMap<>();
        beanMap.put("bean1", new Bean("bean1", new DefaultUserService()));
        beanMap.put("bean2", new Bean("bean2", new DefaultUserService()));
        annotationBasedApplicationContext.setBeans(beanMap);

        assertThrows(NoUniqueBeanOfTypeException.class, () -> {
            annotationBasedApplicationContext.getBean(DefaultUserService.class);
        });
    }

    @Test
    @DisplayName("should get bean by id and clazz successfully")
    void shouldGetBeanByIdAndClazzSuccessfully() {
        Map<String, Bean> beanMap = new HashMap<>();
        DefaultUserService beanValue1 = new DefaultUserService();
        DefaultUserService beanValue2 = new DefaultUserService();
        beanMap.put("bean1", new Bean("bean1", beanValue1));
        beanMap.put("bean2", new Bean("bean2", beanValue2));
        annotationBasedApplicationContext.setBeans(beanMap);
        DefaultUserService actualBeanValue1 = annotationBasedApplicationContext.getBean("bean1", DefaultUserService.class);
        DefaultUserService actualBeanValue2 = annotationBasedApplicationContext.getBean("bean2", DefaultUserService.class);
        assertNotNull(actualBeanValue1);
        assertNotNull(actualBeanValue2);
        assertEquals(beanValue1, actualBeanValue1);
        assertEquals(beanValue2, actualBeanValue2);
    }

    @Test
    @DisplayName("should get bean by id and clazz no such bean successfully")
    void shouldGetBeanByIdAndClazzNoSuchBeanSuccessfully() {
        Map<String, Bean> beanMap = new HashMap<>();
        DefaultUserService beanValue = new DefaultUserService();
        beanMap.put("bean1", new Bean("bean1", beanValue));
        annotationBasedApplicationContext.setBeans(beanMap);

        assertThrows(NoSuchBeanDefinitionException.class, () -> {
            annotationBasedApplicationContext.getBean("bean1", MailService.class);
        });
    }

    @Test
    @DisplayName("should get bean names successfully")
    void shouldGetBeanNamesSuccessfully() {
        Map<String, Bean> beanMap = new HashMap<>();
        beanMap.put("bean3", new Bean("bean3", new DefaultUserService()));
        beanMap.put("bean4", new Bean("bean4", new DefaultUserService()));
        beanMap.put("bean5", new Bean("bean5", new DefaultUserService()));
        annotationBasedApplicationContext.setBeans(beanMap);
        List<String> actualBeansNames = annotationBasedApplicationContext.getBeanNames();
        List<String> expectedBeansNames = Arrays.asList("bean3", "bean4", "bean5");
        assertTrue(actualBeansNames.containsAll(expectedBeansNames));
        assertTrue(expectedBeansNames.containsAll(actualBeansNames));
    }

    @Test
    @DisplayName("should inject value dependencies successfully")
    void shouldInjectValueDependenciesSuccessfully() {
        Map<String, Bean> beanMap = new HashMap<>();
        Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();

        MailService mailServicePOP = new MailService();
        beanMap.put("mailServicePOP", new Bean("mailServicePOP", mailServicePOP));
        MailService mailServiceIMAP = new MailService();
        beanMap.put("mailServiceIMAP", new Bean("mailServiceIMAP", mailServiceIMAP));

        //  setPort(110) and setProtocol("POP3") via valueDependencies
        BeanDefinition popServiceBeanDefinition = new BeanDefinition("mailServicePOP", "com.study.testclasses.MailService");
        Map<String, String> popServiceValueDependencies = new HashMap<>();
        popServiceValueDependencies.put("port", "110");
        popServiceValueDependencies.put("protocol", "POP3");
        popServiceBeanDefinition.setValueDependencies(popServiceValueDependencies);
        beanDefinitionMap.put("mailServicePOP", popServiceBeanDefinition);

        //  setPort(143) and setProtocol("IMAP") via valueDependencies
        BeanDefinition imapServiceBeanDefinition = new BeanDefinition("mailServiceIMAP", "com.study.testclasses.MailService");
        Map<String, String> imapServiceValueDependencies = new HashMap<>();
        imapServiceValueDependencies.put("port", "143");
        imapServiceValueDependencies.put("protocol", "IMAP");
        imapServiceBeanDefinition.setValueDependencies(imapServiceValueDependencies);
        beanDefinitionMap.put("mailServiceIMAP", imapServiceBeanDefinition);

        annotationBasedApplicationContext.injectValueDependencies(beanDefinitionMap, beanMap);
        assertEquals(110, mailServicePOP.getPort());
        assertEquals("POP3", mailServicePOP.getProtocol());
        assertEquals(143, mailServiceIMAP.getPort());
        assertEquals("IMAP", mailServiceIMAP.getProtocol());
    }

    @Test
    @DisplayName("should inject ref dependencies successfully")
    void shouldInjectRefDependenciesSuccessfully() {

        Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();

        BeanDefinition beanDefinitionMailService = new BeanDefinition("MailService", "com.bondarenko.ioc.testclasses.reader.MailService");
        beanDefinitionMap.put("MailService", beanDefinitionMailService);

        BeanDefinition beanDefinitionUserService = new BeanDefinition("DefaultUserService", "com.bondarenko.ioc.testclasses.reader.DefaultUserService");
        beanDefinitionMap.put("DefaultUserService", beanDefinitionUserService);

        BeanDefinition messageService = new BeanDefinition("MessageService", "com.bondarenko.ioc.testclasses.reader.DefaultMessageService");
        beanDefinitionMap.put("MessageService", messageService);

        Map<String, Bean> beanMap = annotationBasedApplicationContext.createBeans(beanDefinitionMap);
        assertEquals(3, (beanMap.size()));

        assertTrue(beanMap.containsKey("DefaultUserService"));
        DefaultUserService userService = (DefaultUserService) beanMap.get("DefaultUserService").getValue();
        assertNull(userService.getMailService());

        annotationBasedApplicationContext.injectRefDependencies(null, beanMap);

        assertNotNull(userService.getMailService());
        assertEquals(143, (userService.getMailService()).getPort());
        assertEquals("IMAP", (userService.getMailService()).getProtocol());
    }

    @Test
    @DisplayName("should inject value successfully")
    void shouldInjectValueSuccessfully() throws ReflectiveOperationException {
        MailService mailService = new MailService();
        Method setPortMethod = MailService.class.getDeclaredMethod("setPort", Integer.TYPE);
        annotationBasedApplicationContext.injectValue(mailService, setPortMethod, "465");
        int actualPort = mailService.getPort();
        assertEquals(465, actualPort);
    }

    @Test
    @DisplayName("test Create BeanPostProcessors")
    void testCreateBeanPostProcessors() {
        Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();
        BeanDefinition beanDefinitionFactoryPostProcessor =
                new BeanDefinition("beanFactoryPostProcessor", "com.bondarenko.ioc.testclasses.processor.CustomBeanFactoryPostProcessor");
        BeanDefinition beanDefinitionUserService =
                new BeanDefinition("userService", "com.bondarenko.ioc.testclasses.reader.DefaultUserService");
        beanDefinitionMap.put("userService", beanDefinitionUserService);
        beanDefinitionMap.put("beanFactoryPostProcessor", beanDefinitionFactoryPostProcessor);
        BeanDefinition beanDefinitionPostProcessor =
                new BeanDefinition("beanPostProcessor", "com.bondarenko.ioc.testclasses.processor.CustomBeanPostProcessor");
        beanDefinitionMap.put("beanPostProcessor", beanDefinitionPostProcessor);

        annotationBasedApplicationContext.createBeanPostProcessors(beanDefinitionMap);
        Map<String, Bean> beanPostProcessors = annotationBasedApplicationContext.getBeanPostProcessorsMap();
        List<BeanFactoryPostProcessor> beanFactoryPostProcessors = annotationBasedApplicationContext.getBeanFactoryPostProcessors();

        assertNotNull(beanPostProcessors);
        assertNotNull(beanFactoryPostProcessors);
        assertEquals(CustomBeanFactoryPostProcessor.class, beanFactoryPostProcessors.get(0).getClass());
    }

    @Test
    @DisplayName("process BeanDefinitions")
    void processBeanDefinitions() {
        Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();
        BeanDefinition beanDefinitionMailService
                = new BeanDefinition("mailServicePOP", "com.bondarenko.ioc.testclasses.reader.MailService");
        beanDefinitionMap.put("mailServicePOP", beanDefinitionMailService);

        BeanDefinition beanDefinitionUserService = new BeanDefinition("userService", "com.bondarenko.ioc.testclasses.reader.DefaultUserService");
        beanDefinitionMap.put("userService", beanDefinitionUserService);

        BeanDefinition beanDefinitionFactoryPostProcessor =
                new BeanDefinition("beanFactoryPostProcessor", "com.bondarenko.ioc.testclasses.processor.CustomBeanFactoryPostProcessor");
        beanDefinitionMap.put("beanFactoryPostProcessor", beanDefinitionFactoryPostProcessor);

        annotationBasedApplicationContext.createBeanPostProcessors(beanDefinitionMap);
        annotationBasedApplicationContext.processBeanDefinitions(beanDefinitionMap);
        Map<String, Bean> beanMap = annotationBasedApplicationContext.createBeans(beanDefinitionMap);
        annotationBasedApplicationContext.injectValueDependencies(beanDefinitionMap, beanMap);

        MailService mailService = (MailService) beanMap.get("mailServicePOP").getValue();
        assertEquals(4500, mailService.getPort());
    }

    @Test
    @DisplayName("should Process Beans Before Initialization Successfully")
    void shouldProcessBeansBeforeInitializationSuccessfully() {
        Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();

        BeanDefinition beanDefinitionMessageService = new BeanDefinition("messageService", "com.bondarenko.ioc.testclasses.processor.MessageService");
        beanDefinitionMap.put("messageService", beanDefinitionMessageService);
        BeanDefinition beanDefinitionMailService =
                new BeanDefinition("mailServiceIMAP", "com.bondarenko.ioc.testclasses.reader.MailService");
        beanDefinitionMap.put("mailServiceIMAP", beanDefinitionMailService);
        BeanDefinition beanDefinitionPostProcessor =
                new BeanDefinition("beanPostProcessor", "com.bondarenko.ioc.testclasses.processor.CustomBeanFactoryPostProcessor");
        beanDefinitionMap.put("beanPostProcessor", beanDefinitionPostProcessor);

        Map<String, Bean> beanMap = annotationBasedApplicationContext.createBeans(beanDefinitionMap);
        annotationBasedApplicationContext.createBeanPostProcessors(beanDefinitionMap);
        annotationBasedApplicationContext.processBeansBeforeInitialization(beanMap);

        Bean actualMessageService = beanMap.get("messageService");
        MessageService messageService = (MessageService) actualMessageService.getValue();
        assertEquals(995, messageService.getPort());
        assertEquals("POP3", messageService.getProtocol());
    }

    @Test
    @DisplayName("should Initialize Beans Successfully")
    public void shouldInitializeBeansSuccessfully() {
        Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();
        BeanDefinition beanDefinitionMailService =
                new BeanDefinition("mailServicePOP", "com.study.entity.MailService");
        beanDefinitionMap.put("mailServiceIMAP", beanDefinitionMailService);
        BeanDefinition beanDefinitionBeanPostProcessor =
                new BeanDefinition("beanPostProcessor", "com.bondarenko.ioc.testclasses.processor.CustomBeanPostProcessor");
        beanDefinitionMap.put("beanPostProcessor", beanDefinitionBeanPostProcessor);

        Map<String, Bean> beanMap = annotationBasedApplicationContext.createBeans(beanDefinitionMap);
        MailService mailServicePOP = new MailService();
        mailServicePOP.setPort(110);
        mailServicePOP.setProtocol("POP3");
        beanMap.put("mailServicePOP", new Bean("mailServicePOP", mailServicePOP));

        annotationBasedApplicationContext.createBeanPostProcessors(beanDefinitionMap);
        annotationBasedApplicationContext.processBeansBeforeInitialization(beanMap);
        annotationBasedApplicationContext.initializeBeans(beanMap);

        assertEquals(4467, mailServicePOP.getPort());
        assertEquals("IMAP", mailServicePOP.getProtocol());
    }

    @Test
    @DisplayName("should Process Beans After Initialization Successfully")
    void shouldProcessBeansAfterInitializationSuccessfully() {
        Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();

        BeanDefinition beanDefinitionMessageService = new BeanDefinition("messageService", "com.bondarenko.ioc.testclasses.processor.MessageService");
        beanDefinitionMap.put("messageService", beanDefinitionMessageService);
        BeanDefinition beanDefinitionMailService =
                new BeanDefinition("mailServiceIMAP", "com.bondarenko.ioc.testclasses.reader.MailService");
        beanDefinitionMap.put("mailServiceIMAP", beanDefinitionMailService);
        BeanDefinition beanDefinitionPostProcessor =
                new BeanDefinition("beanPostProcessor", "com.bondarenko.ioc.testclasses.processor.CustomBeanPostProcessor");
        beanDefinitionMap.put("beanPostProcessor", beanDefinitionPostProcessor);


        Map<String, Bean> beanMap = annotationBasedApplicationContext.createBeans(beanDefinitionMap);
        annotationBasedApplicationContext.createBeanPostProcessors(beanDefinitionMap);
        annotationBasedApplicationContext.processBeansBeforeInitialization(beanMap);
        annotationBasedApplicationContext.initializeBeans(beanMap);
        annotationBasedApplicationContext.processBeansAfterInitialization(beanMap);

        Bean actualMessageService = beanMap.get("messageService");
        MessageService messageService = (MessageService) actualMessageService.getValue();
        assertEquals(6000, messageService.getPort());
        assertEquals("POP3", messageService.getProtocol());
    }
}
