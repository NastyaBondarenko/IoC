package com.study.ioc.processor;

import com.study.ioc.entity.Bean;

public class DefaultBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(String beanName, Object object) {
        Bean bean = new Bean(beanName, object);
        if (beanName.startsWith("userService")) {
            bean.setValue("updatedService");
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(String beanName, Object object) {
        Bean bean = new Bean(beanName, object);
        if (beanName.startsWith("userService")) {
            bean.setValue("newService");
        }
        return bean;
    }
}