package com.study.ioc.service;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MessageService {
    int port=4566;
    String protocol="IMAP";
}