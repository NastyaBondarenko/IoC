package com.study.ioc.service;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MessageService {
    private int port = 4566;
    private String protocol = "IMAP";
}