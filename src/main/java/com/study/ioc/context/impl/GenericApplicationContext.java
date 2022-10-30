package com.study.ioc.context.impl;

import com.study.ioc.context.ApplicationContext;
import com.study.ioc.entity.Bean;
import com.study.ioc.entity.BeanDefinition;
import com.study.ioc.exception.BeanInstantiationException;
import com.study.ioc.exception.NoSuchBeanDefinitionException;
import com.study.ioc.exception.NoUniqueBeanOfTypeException;
import com.study.ioc.exception.ProcessPostConstructException;
import com.study.ioc.processor.BeanFactoryPostProcessor;
import com.study.ioc.processor.BeanPostProcessor;
import com.study.ioc.reader.BeanDefinitionReader;
import com.study.ioc.reader.sax.XmlBeanDefinitionReader;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

@Setter
@Getter
public class GenericApplicationContext implements ApplicationContext {

    private Map<String, Bean> beanMap = new HashMap<>();
    private Map<String, Bean> beanPostProcessorsMap = new HashMap<>();
    private List<BeanFactoryPostProcessor> beanFactoryPostProcessors = new ArrayList<>();

    GenericApplicationContext() {
    }

    public GenericApplicationContext(String... paths) {
        this(new XmlBeanDefinitionReader(paths));
    }

    public GenericApplicationContext(BeanDefinitionReader definitionReader) {
        Map<String, BeanDefinition> beanDefinitions = definitionReader.getBeanDefinition();

        createBeanPostProcessors(beanDefinitions);
        processBeanDefinitions(beanDefinitions);
        beanMap = createBeans(beanDefinitions);
        injectValueDependencies(beanDefinitions, beanMap);
        injectRefDependencies(beanDefinitions, beanMap);
        processBeansBeforeInitialization(beanPostProcessorsMap);
        initializeBeans(beanMap);
        processBeansAfterInitialization(beanPostProcessorsMap);
    }

    @Override
    public Object getBean(String beanId) {
        if (beanMap.containsKey(beanId)) {
            return beanMap.get(beanId).getValue();
        }
        return null;
    }

    @Override
    public <T> T getBean(Class<T> clazz) {
        T beanValue = null;
        for (Map.Entry<String, Bean> entry : beanMap.entrySet()) {
            Bean bean = entry.getValue();
            if (clazz.isAssignableFrom(bean.getValue().getClass())) {
                if (beanValue != null) {
                    throw new NoUniqueBeanOfTypeException("No unique bean of type" + clazz.getName());
                }
                beanValue = clazz.cast(bean.getValue());
            }
        }
        return beanValue;
    }

    @Override
    public <T> T getBean(String id, Class<T> clazz) {
        Class<?> beanClass = null;
        if (beanMap.containsKey(id)) {
            Bean bean = beanMap.get(id);
            beanClass = bean.getValue().getClass();
            if (Objects.equals(clazz, beanClass)) {
                return clazz.cast(bean.getValue());
            }
        }
        throw new NoSuchBeanDefinitionException(id, clazz.getName(), beanClass.getName());
    }

    @Override
    public List<String> getBeanNames() {
        return new ArrayList<>(beanMap.keySet());
    }

    Map<String, Bean> createBeans(Map<String, BeanDefinition> beanDefinitionMap) {
        for (Map.Entry<String, BeanDefinition> beanDefinition : beanDefinitionMap.entrySet()) {
            Object beanObject;
            String key = beanDefinition.getKey();
            try {
                beanObject = Class.forName(beanDefinition.getValue().getClassName()).getConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException |
                     NoSuchMethodException | InvocationTargetException exception) {
                throw new BeanInstantiationException("Can`t create bean`s instantiation", exception);
            }
            Bean bean = new Bean(key, beanObject);
            beanMap.put(key, bean);
        }
        return beanMap;
    }

    public void injectRefDependencies(Map<String, BeanDefinition> beanDefinitions, Map<String, Bean> beans) {
        for (Map.Entry<String, BeanDefinition> entry : beanDefinitions.entrySet()) {
            String key = entry.getKey();
            Bean bean = beans.get(key);
            Map<String, String> refDependencies = entry.getValue().getRefDependencies();

            for (Map.Entry<String, String> refDependency : refDependencies.entrySet()) {
                String beanObject = refDependency.getValue();
                findMethodToInjectRefDependencies(bean, refDependency.getKey(), beans.get(beanObject).getValue());
            }
        }
    }

    public void injectValueDependencies(Map<String, BeanDefinition> beanDefinitions, Map<String, Bean> beans) {
        beans.forEach((key, value) -> {
            if (beanDefinitions.containsKey(key)) {
                Map<String, String> valueDependencies = beanDefinitions.get(key).getValueDependencies();
                Field[] fields = value.getValue().getClass().getDeclaredFields();
                findFieldsToInjectValueDependencies(value, valueDependencies, fields);
            }
        });
    }

    @SneakyThrows
    void injectValue(Object object, Method classMethod, String propertyValue) {
        Method[] methods = object.getClass().getDeclaredMethods();
        String methodName = classMethod.getName();

        List<Method> searchedMethods = Stream.of(methods)
                .filter(method -> method.getName().equals(methodName))
                .filter(method -> method.getParameterTypes().length == 1)
                .toList();
        for (Method searchedMethod : searchedMethods) {
            Class<?>[] parameterTypes = searchedMethod.getParameterTypes();
            String name = parameterTypes[0].getName();
            if (name.equalsIgnoreCase(Integer.TYPE.getName())) {
                int port = Integer.parseInt(propertyValue);
                searchedMethod.invoke(object, port);
            }
        }
    }

    void setBeans(Map<String, Bean> beans) {
        this.beanMap = beans;
    }

    @SneakyThrows
    void createBeanPostProcessors(Map<String, BeanDefinition> beanDefinitionMap) {
        for (Map.Entry<String, BeanDefinition> entry : beanDefinitionMap.entrySet()) {
            Class<?> clazz = Class.forName(entry.getValue().getClassName());

            if ((BeanFactoryPostProcessor.class).isAssignableFrom(clazz)) {
                BeanFactoryPostProcessor beanFactoryPostProcessor =
                        (BeanFactoryPostProcessor) Class.forName(clazz.getName()).getConstructor().newInstance();
                beanFactoryPostProcessors.add(beanFactoryPostProcessor);
            }
            if ((BeanPostProcessor.class).isAssignableFrom(clazz)) {
                BeanDefinition entryValue = entry.getValue();
                BeanPostProcessor beanPostProcessor =
                        (BeanPostProcessor) Class.forName(clazz.getName()).getConstructor().newInstance();
                Bean bean = new Bean(entryValue.getId(), beanPostProcessor);
                beanPostProcessorsMap.put(entry.getKey(), bean);
            }
        }
    }

    void processBeanDefinitions(Map<String, BeanDefinition> beanDefinitionsMap) {
        for (BeanFactoryPostProcessor beanFactoryPostProcessor : beanFactoryPostProcessors) {
            beanFactoryPostProcessor.postProcessBeanFactory(beanDefinitionsMap);
        }
    }

    void processBeansBeforeInitialization(Map<String, Bean> beanMap) {
        List<Bean> beanPostProcessors = beanPostProcessorsMap.values().stream().toList();
        for (Bean beanPostProcessor : beanPostProcessors) {
            BeanPostProcessor objectBeanPostProcessor = (BeanPostProcessor) beanPostProcessor.getValue();
            for (Map.Entry<String, Bean> entry : beanMap.entrySet()) {
                Bean bean = entry.getValue();
                String beanId = bean.getId();

                Bean beanProcessed = objectBeanPostProcessor.postProcessBeforeInitialization(beanId, bean);
                beanMap.put(beanId, beanProcessed);
            }
        }
    }

    void initializeBeans(Map<String, Bean> beanMap) {
        for (Bean bean : beanMap.values()) {
            Object beanObject = bean.getValue();
            for (Method declaredMethod : beanObject.getClass().getDeclaredMethods()) {
                if (declaredMethod.isAnnotationPresent(PostConstruct.class)) {
                    try {
                        declaredMethod.setAccessible(true);
                        declaredMethod.invoke(beanObject);
                    } catch (Exception exception) {
                        throw new ProcessPostConstructException("Can`t invoke method to initialize beans", exception);
                    }
                }
            }
        }
    }

    void processBeansAfterInitialization(Map<String, Bean> beanMap) {
        List<Bean> beanPostProcessors = beanPostProcessorsMap.values().stream().toList();
        for (Bean beanPostProcessor : beanPostProcessors) {
            BeanPostProcessor objectBeanPostProcessor = (BeanPostProcessor) beanPostProcessor.getValue();
            for (Map.Entry<String, Bean> entry : beanMap.entrySet()) {
                Bean bean = entry.getValue();
                String beanId = bean.getId();

                Bean beanProcessed = objectBeanPostProcessor.postProcessAfterInitialization(beanId, bean);
                beanMap.put(beanId, beanProcessed);
            }
        }
    }

    private void findFieldsToInjectValueDependencies(Bean value, Map<String, String> valueDependencies, Field[] fields) {
        for (Field field : fields) {
            if (valueDependencies.containsKey(field.getName())) {
                try {
                    Class<?> fieldType = field.getType();
                    String valueDependency = valueDependencies.get(field.getName());
                    field.setAccessible(true);
                    if ((Integer.TYPE == fieldType)) {
                        Object object = Integer.parseInt(valueDependency);
                        field.set(value.getValue(), object);
                        return;
                    }
                    field.set(value.getValue(), String.valueOf(valueDependency));
                } catch (IllegalAccessException exception) {
                    throw new BeanInstantiationException("Cant find fields to inject valueDependencies", exception);
                }
            }
        }
    }

    @SneakyThrows
    private void findMethodToInjectRefDependencies(Bean bean, String fieldName, Object value) {
        Method[] methods = bean.getValue().getClass().getDeclaredMethods();
        String methodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                method.invoke(bean.getValue(), value);
            }
        }
    }
}