package com.bondarenko.ioc.testclasses.processor;

import com.bondarenko.ioc.annotation.Order;
import com.bondarenko.ioc.processor.BeanPostProcessor;

@Order(2)
public class CustomBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(String beanName, Object beanValue) {
        if (beanValue.getClass().equals(MessageService.class)) {
            MessageService messageService = new MessageService();
            messageService.setPort(5000);
            messageService.setProtocol("POP");
            beanValue = messageService;
        }
        return beanValue;
    }

    @Override
    public Object postProcessAfterInitialization(String beanName, Object beanValue) {
        if (beanValue.getClass().equals(MessageService.class)) {
            MessageService messageService = new MessageService();
            messageService.setPort(6000);
            beanValue = messageService;
        }
        return beanValue;
    }
}