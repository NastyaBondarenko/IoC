package com.study.ioc.reader.sax;

import com.study.ioc.entity.BeanDefinition;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

class BeanDefinitionScannerTest {

    private final String[] SCAN_PACKAGES = {"com.study.testclasses"};

    @Test
    @DisplayName("should scan bean definition successfully")
    void shouldScanBeanDefinitionSuccessfully() {
        BeanDefinitionScanner scanner = new BeanDefinitionScanner(SCAN_PACKAGES);
        Map<String, BeanDefinition> beanDefinitionMap = scanner.getBeanDefinition();

        Assertions.assertEquals(3, beanDefinitionMap.size());

        BeanDefinition beanDefinitionFirst = beanDefinitionMap.get("MailServicePOP");
        Assertions.assertEquals("MailServicePOP", beanDefinitionFirst.getId());
        Assertions.assertTrue(beanDefinitionFirst.getRefDependencies().isEmpty());

        BeanDefinition beanDefinitionSecond = beanDefinitionMap.get("UserService");
        Assertions.assertEquals("UserService", beanDefinitionSecond.getId());
        Assertions.assertTrue(beanDefinitionSecond.getValueDependencies().isEmpty());

        BeanDefinition beanDefinitionThird = beanDefinitionMap.get("MailServiceIMAP");
        Assertions.assertEquals("MailServiceIMAP", beanDefinitionThird.getId());
        Assertions.assertTrue(beanDefinitionThird.getRefDependencies().isEmpty());
    }
}