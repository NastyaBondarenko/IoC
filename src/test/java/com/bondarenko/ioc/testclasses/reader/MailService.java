package com.bondarenko.ioc.testclasses.reader;

import com.bondarenko.ioc.annotation.Component;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Component
public class MailService {

    private int port = 143;
    private String protocol = "IMAP";
}