package com.study.testclasses;

import com.study.ioc.annotations.Autowired;
import com.study.ioc.annotations.Component;

@Component
public class UserService {
    @Autowired
    private MailServicePOP mailService;
}