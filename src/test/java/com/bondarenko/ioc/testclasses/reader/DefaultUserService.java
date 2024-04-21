package com.bondarenko.ioc.testclasses.reader;

import com.bondarenko.ioc.annotation.Autowired;


public class DefaultUserService {

    @Autowired
    private MailService mailService;

    public void setMailService(MailService mailService) {
        this.mailService = mailService;
    }

    public MailService getMailService() {
        return mailService;
    }
}