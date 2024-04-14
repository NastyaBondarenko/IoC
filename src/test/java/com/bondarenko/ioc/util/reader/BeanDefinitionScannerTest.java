package com.bondarenko.ioc.util.reader;

import com.bondarenko.ioc.entity.BeanDefinition;
import com.bondarenko.ioc.util.reader.impl.BeanDefinitionScanner;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

class BeanDefinitionScannerTest {

    private final String[] SCAN_PACKAGES = {"com.bondarenko.ioc.testclasses.reader"};

    @Test
    @DisplayName("should scan bean definition successfully")
    void shouldScanBeanDefinitionSuccessfully() {
        BeanDefinitionScanner beanDefinitionScanner = new BeanDefinitionScanner(SCAN_PACKAGES);
        Map<String, BeanDefinition> beanDefinitionMap = beanDefinitionScanner.getBeanDefinition();

        Assertions.assertEquals(15, beanDefinitionMap.size());

        BeanDefinition beanDefinitionFirst = beanDefinitionMap.get("DefaultUserService");
        Assertions.assertEquals("DefaultUserService", beanDefinitionFirst.getId());
        Assertions.assertEquals("com.bondarenko.ioc.testclasses.reader.DefaultUserService", beanDefinitionFirst.getClassName());
        Assertions.assertTrue(beanDefinitionFirst.getRefDependencies().isEmpty());

        BeanDefinition beanDefinitionSecond = beanDefinitionMap.get("MailService");
        Assertions.assertEquals("MailService", beanDefinitionSecond.getId());
        Assertions.assertEquals("com.bondarenko.ioc.testclasses.reader.MailService", beanDefinitionSecond.getClassName());
        Assertions.assertTrue(beanDefinitionSecond.getRefDependencies().isEmpty());

        BeanDefinition beanDefinitionThird = beanDefinitionMap.get("MessageService");
        Assertions.assertEquals("MessageService", beanDefinitionThird.getId());
        Assertions.assertEquals("com.bondarenko.ioc.testclasses.reader.MessageService", beanDefinitionThird.getClassName());
        Assertions.assertTrue(beanDefinitionThird.getRefDependencies().isEmpty());
    }
}