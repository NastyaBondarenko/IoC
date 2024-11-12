package com.bondarenko.ioc.util.reader;

import com.bondarenko.ioc.entity.BeanDefinition;
import com.bondarenko.ioc.util.reader.impl.AnnotationBeanDefinitionReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

class AnnotationBeanDefinitionReaderTest {


    private final String[] SCAN_PACKAGES = {"com.bondarenko.ioc.testclasses.reader"};

    @Test
    @DisplayName("should scan bean definition successfully")
    void shouldScanBeanDefinitionSuccessfully() {
        AnnotationBeanDefinitionReader annotationBeanDefinitionReader = new AnnotationBeanDefinitionReader(SCAN_PACKAGES);
        Map<String, BeanDefinition> beanDefinitionMap = annotationBeanDefinitionReader.getBeanDefinition();

        Assertions.assertEquals(2, beanDefinitionMap.size());

        BeanDefinition beanDefinitionFirst = beanDefinitionMap.get("DefaultMessageService");
        Assertions.assertEquals("DefaultMessageService", beanDefinitionFirst.getId());
        Assertions.assertEquals("com.bondarenko.ioc.testclasses.reader.DefaultMessageService", beanDefinitionFirst.getClassName());
        Assertions.assertTrue(beanDefinitionFirst.getRefDependencies().isEmpty());

        BeanDefinition beanDefinitionSecond = beanDefinitionMap.get("MailService");
        Assertions.assertEquals("MailService", beanDefinitionSecond.getId());
        Assertions.assertEquals("com.bondarenko.ioc.testclasses.reader.MailService", beanDefinitionSecond.getClassName());
        Assertions.assertTrue(beanDefinitionSecond.getRefDependencies().isEmpty());
    }
}