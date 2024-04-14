package com.bondarenko.ioc.context;

import java.util.List;

public interface ApplicationContext {
    Object getBean(String beanId);
    <T> T getBean(Class<T> clazz);
    <T> T getBean(String id, Class<T> clazz);
    List<String> getBeanNames();

}
