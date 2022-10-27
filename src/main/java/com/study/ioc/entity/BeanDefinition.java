package com.study.ioc.entity;

import java.util.Map;

public class BeanDefinition {
    private String id;
    private String beanClassName;
    private Map<String, String> valueDependencies;
    private Map<String, String> refDependencies;

    public BeanDefinition(String id, String className) {
        this.id = id;
        this.beanClassName = className;
    }

    public String getId() {
        return id;
    }

    public String getClassName() {
        return beanClassName;
    }

    public Map<String, String> getValueDependencies() {
        return valueDependencies;
    }

    public void setValueDependencies(Map<String, String> valueDependencies) {
        this.valueDependencies = valueDependencies;
    }

    public Map<String, String> getRefDependencies() {
        return refDependencies;
    }

    public void setRefDependencies(Map<String, String> refDependencies) {
        this.refDependencies = refDependencies;
    }
}
