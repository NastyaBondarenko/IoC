package com.study.ioc.entity;

import java.util.Map;

public class BeanDefinition {
    private String id;
    private String className;
    private Map<String, String> valueDependencies;
    private Map<String, String> refDependencies;

    public BeanDefinition(String id, String className) {
        this.id = id;
        this.className = className;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
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
