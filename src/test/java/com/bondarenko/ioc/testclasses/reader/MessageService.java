package com.bondarenko.ioc.testclasses.reader;

import com.bondarenko.ioc.annotation.Component;
import lombok.Setter;


@Setter
@Component
public class MessageService {

    private int port = 995;
    private String protocol = "POP3";
}