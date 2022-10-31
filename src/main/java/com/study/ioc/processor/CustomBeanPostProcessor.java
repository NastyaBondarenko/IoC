package com.study.ioc.processor;

import com.study.ioc.entity.Bean;
import com.study.ioc.service.MessageService;

public class CustomBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(String beanName, Bean bean) {
        Object object = bean.getValue();
        if (bean.getValue().getClass().equals(MessageService.class)) {
            MessageService messageService = new MessageService();
            messageService.setPort(5000);
            messageService.setProtocol("POP");
            object = messageService;
        }
        return object;
    }

    @Override
    public Object postProcessAfterInitialization(String beanName, Bean bean) {
        Object object = bean.getValue();
        if (bean.getValue().getClass().equals(MessageService.class)) {
            MessageService messageService = new MessageService();
            messageService.setPort(6000);
            object = messageService;
        }
        return object;
    }
}