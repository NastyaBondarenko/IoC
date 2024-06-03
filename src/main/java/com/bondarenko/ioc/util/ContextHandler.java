package com.bondarenko.ioc.util;

import com.bondarenko.ioc.entity.BeanDefinition;
import com.bondarenko.ioc.exception.ParseContextException;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class ContextHandler extends DefaultHandler {

    private Deque<BeanDefinition> beanDefinitions;

    @Override
    public void startDocument() {
        beanDefinitions = new LinkedList<>();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        if (qName.equalsIgnoreCase("bean")) {
            String id = attributes.getValue("id");
            if (id == null) {
                throw new ParseContextException("No specified id for bean");
            }
            String clazzName = attributes.getValue("class");
            if (clazzName == null) {
                throw new ParseContextException("No specified class for bean");
            }
            BeanDefinition beanDefinition = new BeanDefinition(id, clazzName);
            beanDefinitions.push(beanDefinition);
        } else if (qName.equalsIgnoreCase("property")) {
            String propertyName = attributes.getValue("name");
            if (propertyName == null) {
                throw new ParseContextException("No specified name for property");
            }
            BeanDefinition beanDefinition = beanDefinitions.peekLast();
            String propertyValue = attributes.getValue("value");
            String propertyRef = attributes.getValue("ref");
            if (propertyValue != null) {
                beanDefinition.getValueDependencies().put(propertyName, propertyValue);
            }
            if (propertyRef != null) {
                beanDefinition.getValueDependencies().put(propertyName, propertyValue);
            }
        }
    }

    public Map<String, BeanDefinition> getBeanDefinitions() {
        return beanDefinitions.stream()
                .collect(HashMap::new, (map, beanDefinition) -> map.put(beanDefinition.getId(), beanDefinition), Map::putAll);
    }
}
