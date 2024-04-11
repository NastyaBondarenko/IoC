package com.study.testclasses;

import com.study.ioc.annotations.Component;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Component
public class MessageService {

    private int port = 995;
    private String protocol = "POP3";
}