package com.bondarenko.ioc.util.reader.impl;

import com.bondarenko.ioc.entity.BeanDefinition;
import com.bondarenko.ioc.exception.ParseContextException;
import com.bondarenko.ioc.util.ContextHandler;
import com.bondarenko.ioc.util.reader.BeanDefinitionReader;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class XmlBeanDefinitionReader implements BeanDefinitionReader {
    private String[] paths;

    public XmlBeanDefinitionReader(String... paths) {
        this.paths = paths;
    }

    @Override
    public Map<String, BeanDefinition> getBeanDefinition() {
        Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();
        for (String path : paths) {
            ClassLoader classLoader = getClass().getClassLoader();
            try (InputStream inputStream = new BufferedInputStream(classLoader.getResourceAsStream(path))) {
                beanDefinitionMap.putAll(getBeanDefinitionMap(inputStream));
            } catch (IOException | ParserConfigurationException | SAXException e) {
                throw new ParseContextException("Context parse failed for " + path);
            }
        }
        return beanDefinitionMap;
    }


    public Map<String, BeanDefinition> getBeanDefinitionMap(InputStream inputStream) throws ParserConfigurationException, SAXException, IOException {
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        SAXParser saxParser = saxParserFactory.newSAXParser();
        ContextHandler handler = new ContextHandler();
        saxParser.parse(inputStream, handler);
        return handler.getBeanDefinitions();
    }
}
