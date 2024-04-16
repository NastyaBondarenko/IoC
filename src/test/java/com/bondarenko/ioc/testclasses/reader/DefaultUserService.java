package com.bondarenko.ioc.testclasses.reader;

import com.bondarenko.ioc.annotation.Autowired;
import com.bondarenko.ioc.annotation.Component;
import com.bondarenko.ioc.service.MessageService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DefaultUserService {

    @Autowired
    private MessageService mailService;
}