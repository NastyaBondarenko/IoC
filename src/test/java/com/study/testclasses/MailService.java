package com.study.testclasses;

import com.study.ioc.annotations.Component;

@Component
public class MailService {

    private int port = 143;
    private String protocol = "IMAP";
}