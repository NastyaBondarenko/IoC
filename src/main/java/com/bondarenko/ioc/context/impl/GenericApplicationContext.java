package com.bondarenko.ioc.context.impl;

import com.bondarenko.ioc.context.ApplicationContext;
import com.bondarenko.ioc.entity.Bean;
import com.bondarenko.ioc.entity.BeanDefinition;
import com.bondarenko.ioc.exception.BeanInstantiationException;
import com.bondarenko.ioc.exception.NoSuchBeanDefinitionException;
import com.bondarenko.ioc.exception.NoUniqueBeanOfTypeException;
import com.bondarenko.ioc.exception.ProcessPostConstructException;
import com.bondarenko.ioc.processor.BeanFactoryPostProcessor;
import com.bondarenko.ioc.processor.BeanPostProcessor;
import com.bondarenko.ioc.util.reader.BeanDefinitionReader;
import com.bondarenko.ioc.util.reader.impl.XmlBeanDefinitionReader;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import javax.annotation.PostConstruct;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
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
        processBeansBeforeInitialization(beanMap);
        initializeBeans(beanMap);
        processBeansAfterInitialization(beanMap);
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
            if (!refDependencies.isEmpty()) {
                for (Map.Entry<String, String> refDependency : refDependencies.entrySet()) {
                    String beanObject = refDependency.getValue();
                    findMethodToInjectRefDependencies(bean, refDependency.getKey(), beans.get(beanObject).getValue());
                }
            }
        }
    }

    public void injectValueDependencies(Map<String, BeanDefinition> beanDefinitionMap, Map<String, Bean> beanMap) {
        beanMap.forEach((beanKey, beanValue) -> {
            if (beanDefinitionMap.containsKey(beanKey)) {
                Map<String, String> valueDependencies = beanDefinitionMap.get(beanKey).getValueDependencies();
                Bean bean = beanMap.get(beanKey);
                valueDependencies.forEach((key, value) -> findMethodsToInjectValueDependencies(bean, key, value));
            }
        });
    }

    @SneakyThrows
    void injectValue(Object object, Method classMethod, String propertyValue) {
        Method[] methods = object.getClass().getMethods();
        String methodName = classMethod.getName();
        Class<?>[] parameterTypes = classMethod.getParameterTypes();

        List<Method> searchedMethods = Stream.of(methods)
                .filter(method -> method.getName().equals(methodName))
                .filter(method -> method.getParameterTypes().length == 1)
                .toList();

        if (!searchedMethods.isEmpty()) {
            String parameterType = parameterTypes[0].getName();
            if (parameterType.equals(Integer.TYPE.getName())) {
                int intValueOfProperty = Integer.parseInt(propertyValue);
                classMethod.invoke(object, intValueOfProperty);
                return;
            }
        }
        classMethod.invoke(object, propertyValue);
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
                BeanDefinition beanDefinition = entry.getValue();
                BeanPostProcessor beanPostProcessor =
                        (BeanPostProcessor) Class.forName(clazz.getName()).getConstructor().newInstance();
                Bean bean = new Bean(beanDefinition.getId(), beanPostProcessor);
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

                Object object = objectBeanPostProcessor.postProcessBeforeInitialization(beanId, bean);
                bean.setValue(object);
                beanMap.put(beanId, bean);
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
        for (Map.Entry<String, Bean> entry : beanPostProcessorsMap.entrySet()) {
            Bean serviceBean = entry.getValue();
            BeanPostProcessor objectPostProcessor = (BeanPostProcessor) serviceBean.getValue();

            for (Map.Entry<String, Bean> beanEntry : beanMap.entrySet()) {
                String beanId = beanEntry.getValue().getId();
                Bean bean = beanEntry.getValue();

                Object object = objectPostProcessor.postProcessAfterInitialization(beanId, bean);
                bean.setValue(object);
                beanMap.put(beanId, bean);
            }
        }
    }

    @SneakyThrows
    void findMethodToInjectRefDependencies(Bean bean, String fieldName, Object value) {
        Method[] methods = bean.getValue().getClass().getMethods();
        String methodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                method.invoke(bean.getValue(), value);
            }
        }
    }

    @SneakyThrows
    private void findMethodsToInjectValueDependencies(Bean bean, String key, String value) {
        List<Method> methods = Arrays.stream(bean.getValue().getClass().getMethods()).toList();
        String methodName = "set" + key.substring(0, 1).toUpperCase() + key.substring(1);

        Class<?>[] parameterTypes = methods.stream()
                .filter(method -> method.getName().equals(methodName))
                .findFirst()
                .map(Method::getParameterTypes)
                .get();

        String parameterType = Arrays.stream(parameterTypes).findFirst().get().getName();
        Method searchedMethod = methods.stream()
                .filter(method -> method.getName().equals(methodName))
                .findFirst().get();

        if (parameterType.equals(Integer.TYPE.getName()) || parameterType.equals(String.class.getName())) {
            injectValue(bean.getValue(), searchedMethod, value);
        }
    }
}