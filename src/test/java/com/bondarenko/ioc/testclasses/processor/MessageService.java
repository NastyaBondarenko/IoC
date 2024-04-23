package com.bondarenko.ioc.testclasses.processor;

import com.bondarenko.ioc.annotation.Component;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Component
public class MessageService {

    private int port = 995;
    private String protocol = "POP3";
}