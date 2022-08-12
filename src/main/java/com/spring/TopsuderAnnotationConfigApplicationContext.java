package com.spring;

import java.beans.Introspector;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.security.Key;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * <span>Form File</span>
 * <p>Description</p>
 * <p>Copyright: Copyright (c) 2022 版权</p>
 * <p>Company:QQ 752340543</p>
 *
 * @author topsuder
 * @version v1.0.0
 * @DATE 2022/8/12-09:54
 * @Description
 * @see com.spring mock-spring
 */
public class TopsuderAnnotationConfigApplicationContext {

    private Class configClass;

    private Map<String,BeanDefinition> beanDefinitionMap =  new HashMap<>();

    private Map<String, Object> singletonObjects = new HashMap<>();

    private List<BeanPostProcessor> beanPostProcessors = new LinkedList<>();


    public TopsuderAnnotationConfigApplicationContext(Class configClass) {
        this.configClass=configClass;

        // 扫描
        scan(configClass);


        beanDefinitionMap.forEach((k,v)->{
            if (v.getScope().equals("singleton")){
                singletonObjects.put(k,createBean(k,v));
            }
        });
    }


    private Object createBean(String beanName,BeanDefinition beanDefinition){

        final Class clazz = beanDefinition.getType();

       Object instance = null;
        try {
            instance = clazz.getConstructor().newInstance();


            //依赖注入
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(Autowired.class)) {
                    field.setAccessible(true);
                    field.set(instance,getBean(field.getName()));
                }
            }


            //判断是否实现beanAware接口
            if (instance instanceof BeanNameAware) {
                ((BeanNameAware) instance).setBeanName(beanName);
            }


            //初始化之前
            for (BeanPostProcessor beanPostProcessor : beanPostProcessors) {
                instance = beanPostProcessor.postProcessBeforeInitialization(instance, beanName);
            }


            //初始化
            if (instance instanceof InitializingBean) {
                ((InitializingBean) instance).afterPropertiesSet();
            }

            //初始化之后
            for (BeanPostProcessor beanPostProcessor : beanPostProcessors) {
                instance = beanPostProcessor.postProcessAfterInitialization(instance, beanName);
            }


            return instance;
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


    public Object getBean(String beanName) {
        //beanName-->xx.class-->
        if (!beanDefinitionMap.containsKey(beanName)){
            throw new NullPointerException();
        }

        final BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        if (beanDefinition.getScope().equals("singleton")) {
            Object singletonBean = singletonObjects.get(beanName);
            if (singletonBean == null){
                singletonBean = createBean(beanName,beanDefinition);
                singletonObjects.put(beanName,singletonBean);
            }
            return singletonBean;
        }else {
            final Object prototypeBean = createBean(beanName, beanDefinition);
            return prototypeBean;
        }

    }



    private void scan(Class configClass) {
        if (configClass.isAnnotationPresent(ComponentScan.class)) {
            ComponentScan componentScanAnnotation = (ComponentScan) configClass.getAnnotation(ComponentScan.class);
            String path = componentScanAnnotation.value();

            path = path.replace(".", "/");


            final ClassLoader classLoader = configClass.getClassLoader();
            final URL resource = classLoader.getResource(path);
            assert resource != null;
            File file = new File(resource.getFile());
            if (file.isDirectory()) {
                for (File listFile : Objects.requireNonNull(file.listFiles())) {

                    String absolutePath = listFile.getAbsolutePath();

                    absolutePath = absolutePath.substring(absolutePath.indexOf("com"),absolutePath.indexOf(".class"));
                    absolutePath = absolutePath.replace("/",".");

                    try {
                        final Class<?> clazz = classLoader.loadClass(absolutePath);
                        if (clazz.isAnnotationPresent(Component.class)) {

                            //是否有BeanPostProcessor
                            if (BeanPostProcessor.class.isAssignableFrom(clazz)){

                                BeanPostProcessor beanPostProcessor = (BeanPostProcessor) clazz.getConstructor().newInstance();
                                beanPostProcessors.add(beanPostProcessor);
                            }


                            final Component annotationAnnotation = clazz.getAnnotation(Component.class);
                            String beanName = annotationAnnotation.value();

                            if ("".equals(beanName)){
                                beanName = Introspector.decapitalize(clazz.getSimpleName());
                            }


                            BeanDefinition beanDefinition = new BeanDefinition();

                            beanDefinition.setType(clazz);

                            if (clazz.isAnnotationPresent(Scope.class)) {

                                final Scope annotation = clazz.getAnnotation(Scope.class);
                                String value = annotation.value();
                                beanDefinition.setScope(value);

                            }else {

                                beanDefinition.setScope("singleton");

                            }


                            beanDefinitionMap.put(beanName,beanDefinition);


                        }



                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    } catch (InvocationTargetException e) {
                        throw new RuntimeException(e);
                    } catch (InstantiationException e) {
                        throw new RuntimeException(e);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    } catch (NoSuchMethodException e) {
                        throw new RuntimeException(e);
                    }

                }
            }

        }
    }
}
