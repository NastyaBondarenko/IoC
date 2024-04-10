package com.study.testclasses;

import com.study.ioc.annotations.Autowired;
import com.study.ioc.annotations.Component;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DefaultUserService {

    @Autowired
    private MessageService mailService;
}