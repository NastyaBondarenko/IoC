package com.bondarenko.ioc.testclasses.context.impl;

import com.bondarenko.ioc.testclasses.context.IMailService;
import com.bondarenko.ioc.testclasses.context.UserService;


import java.util.ArrayList;
import java.util.List;

public class UserServiceImpl implements UserService {

    private IMailService mailService;

    public void activateUsers() {
        System.out.println("Get users from db");

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
