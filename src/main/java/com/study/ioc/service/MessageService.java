package com.study.ioc.service;

import com.study.ioc.annotations.Component;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Component
public class MessageService {
    private int port = 4566;
    private String protocol = "IMAP";
}