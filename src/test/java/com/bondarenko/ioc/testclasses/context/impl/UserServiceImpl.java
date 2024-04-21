package com.bondarenko.ioc.testclasses.context.impl;

import com.bondarenko.ioc.annotation.Autowired;
import com.bondarenko.ioc.testclasses.context.IMailService;
import com.bondarenko.ioc.testclasses.context.UserService;

import java.util.ArrayList;
import java.util.List;

public class UserServiceImpl implements UserService {
    @Autowired
    private IMailService mailService;

    public void activateUsers() {
        List<User> users = new ArrayList<>();
        for (User user : users) {
            mailService.sendEmail(user, "You are active now");
        }
    }

    public void setMailService(IMailService mailService) {
        this.mailService = mailService;
    }

    public IMailService getMailService() {
        return mailService;
    }
}
