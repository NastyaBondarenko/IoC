package com.bondarenko.ioc.testclasses.processor;

import com.bondarenko.ioc.annotation.Autowired;


public class TestBean {

    @Autowired
    private MessageService messageService;

    public MessageService getMessageService() {
        return messageService;
    }

    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }
}