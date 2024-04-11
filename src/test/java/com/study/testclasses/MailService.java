package com.study.testclasses;

import com.study.ioc.annotations.Component;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Component
public class MailService {

    private int port = 143;
    private String protocol = "IMAP";
}