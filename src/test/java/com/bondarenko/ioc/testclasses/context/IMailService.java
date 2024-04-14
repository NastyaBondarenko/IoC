package com.bondarenko.ioc.testclasses.context;

import com.bondarenko.ioc.testclasses.context.impl.User;

public interface IMailService {
    void sendEmail(User user, String message);
}
