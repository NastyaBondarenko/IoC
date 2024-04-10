package com.study.testclasses;

import com.study.ioc.annotations.Component;

@Component
public class MessageService {
    private int port = 995;
    private String protocol = "POP3";
}