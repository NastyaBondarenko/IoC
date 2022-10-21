package com.study.ioc.reader.sax;

import com.study.ioc.entity.BeanDefinition;
import com.study.ioc.exception.ParseContextException;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.util.HashMap;
import java.util.Map;

public class ContextHandler extends DefaultHandler {

    private Map<String, BeanDefinition> beanDefinitions;
    private BeanDefinition beanDefinition;
    private Map<String, String> valueDependencies;
    private Map<String, String> refDependencies;

    private boolean bBean;

    @Override
    public void startDocument() {
        beanDefinitions = new HashMap<>();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        if (qName.equalsIgnoreCase("bean")) {
            bBean = true;
            String id = attributes.getValue("id");
            if (id == null) {
                throw new ParseContextException("No specified id for bean");
            }
            String clazzName = attributes.getValue("class");
            if (clazzName == null) {
                throw new ParseContextException("No specified class for bean");
            }
            beanDefinition = new BeanDefinition(id, clazzName);
            valueDependencies = new HashMap<>();
            refDependencies = new HashMap<>();
        } else if (qName.equalsIgnoreCase("property")) {
            if (!bBean) {
                throw new ParseContextException("No specified bean for property");
            }
            String propertyName = attributes.getValue("name");
            if (propertyName == null) {
                throw new ParseContextException("No specified name for property");
            }
            String propertyValue = attributes.getValue("value");
            String propertyRef = attributes.getValue("ref");
            if (propertyValue != null) {
                valueDependencies.put(propertyName, propertyValue);
            }
            if (propertyRef != null) {
                refDependencies.put(propertyName, propertyRef);
            }
        }

    }

    @Override
    public void endElement(String uri,
                           String localName, String qName) {
        if (qName.equalsIgnoreCase("bean")) {
            bBean = false;
            beanDefinition.setValueDependencies(valueDependencies);
            beanDefinition.setRefDependencies(refDependencies);
            beanDefinitions.put(beanDefinition.getId(), beanDefinition);
        }
    }

    public Map<String, BeanDefinition> getBeanDefinitions() {
        return beanDefinitions;
    }


}
