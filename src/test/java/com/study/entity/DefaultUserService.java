package com.study.entity;

import java.util.ArrayList;
import java.util.List;

public class DefaultUserService implements UserService {

    private IMailService mailService;

    public void activateUsers() {
        System.out.println("Get users from db");

        List<User> users = new ArrayList<>(); // userDao.getAll();
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
