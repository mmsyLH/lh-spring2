package asia.lhweb.ioc;

import asia.lhweb.annotation.Component;
import asia.lhweb.annotation.ComponentScan;
import asia.lhweb.annotation.Scope;
import asia.lhweb.config.SpringConfig;

import java.io.File;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author :罗汉
 * @date : 2024/4/11
 */
import java.lang.annotation.Annotation;

public class MyApplicationContext {
    private Class<SpringConfig> configClass; // 基于注解存放的是配置文件类
    private String[] basePackagePaths;
    private final ConcurrentHashMap<String, BeanDefinition> beanDefinitonMap = new ConcurrentHashMap<>(); // 存放BeanDefiniton对象
    private final ConcurrentHashMap<String, Object> singletonObjects = new ConcurrentHashMap<>(); // 存放单例对象

    public MyApplicationContext(Class<SpringConfig> configClass) {
        this.configClass = configClass;
        // 1、得到需要扫描包的路径 将扫描后的包路径存在scanBasePackagePaths数组中
        getScanBasePackagePath(configClass);

        // 2、循环扫描包 得到含有注解的类全路径并存在beanDefinitonMap中
        for (String basePackagePath : basePackagePaths) {
            scanPackagePath(basePackagePath);
        }

        // 3、统一初始化创建全部单例bean
        initSingletonBeans();
    }

    /**
     * 初始化单例bean
     */
    private void initSingletonBeans() {
        for (String beanName : beanDefinitonMap.keySet()) {
            BeanDefinition beanDefinition = beanDefinitonMap.get(beanName);
            if (beanDefinition.getScope().equals("singleton")) {
                Object bean = createBean(beanDefinition);
                singletonObjects.put(beanName, bean);
            }
        }
    }

    /**
     * 创建bean
     *
     * @param beanDefinition bean定义
     * @return {@link Object}
     */
    private Object createBean(BeanDefinition beanDefinition) {
        ClassLoader classLoader = MyApplicationContext.class.getClassLoader();
        try {
            Class<?> aClass = classLoader.loadClass(beanDefinition.getClassName());
            // 创建bean对象
            return aClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // 如果反射创建对象失败
    }

    /**
     * 扫描包路径 并且封装到BeanDefinition对象，再放入到Map中
     *
     * @param basePackagePath 基本包路径
     */
    private void scanPackagePath(String basePackagePath) {
        // 2.1.1得到类加载器
        ClassLoader classLoader = MyApplicationContext.class.getClassLoader();
        if (classLoader == null) {
            throw new IllegalArgumentException("类加载器为空");
        }

        // 2.1.2 得到转换成/后的路径
        basePackagePath = basePackagePath.replace(".", "/");

        // 2.1.3 判断该路径下是否存在还有文件 如果没有文件就返回
        URL resource = classLoader.getResource(basePackagePath);
        if (resource == null) {
            return;
        }
        File fileTemp = new File(resource.getFile());
        // 2.2.0 遍历该路径下的文件,递归处理 如果是文件就进行判断
        for (File file : fileTemp.listFiles()) {
            if (file.isDirectory()) {
                scanPackagePath(basePackagePath + "/" + file.getName());
            } else {
                //    封装一个将扫描后的包路径存在scanBasePackagePaths数组中的方法 另外定义一个方法 传file进去
                putBeanDefinition(file, basePackagePath);
            }
        }

    }

    /**
     * 放置bean定义
     *
     * @param file 文件
     */
    private void putBeanDefinition(File file, String basePackagePath) {
        // System.out.println(file1);
        String absolutePath = file.getAbsolutePath();
        // 只处理class文件
        if (absolutePath.endsWith(".class")) {
            // 1 获取类名
            String className = absolutePath.substring(absolutePath.lastIndexOf("\\") + 1, absolutePath.indexOf(".class"));

            // 2 获取类的完整路径
            String classFullName = basePackagePath.replace("/", ".") + "." + className;

            // 3 判断该类是不是需要注入到容器中
            try {
                // 反射一个类对象
                // 1 Class.forName 调用该类的静态方法
                // 2 classLoader.loadClass 不会调用该类的静态方法
                // 3 isAnnotationPresent判断该类是否有这个注解
                Class<?> clazz = MyApplicationContext.class.getClassLoader().loadClass(classFullName);
                if (clazz.isAnnotationPresent(Component.class)) {
                    if (clazz.isAnnotationPresent(Component.class)) {
                        System.out.println("这是一个LHSpring bean=" + clazz + "    类名=" + className);
                        Component declaredAnnotation = clazz.getDeclaredAnnotation(Component.class);
                        String beanName = declaredAnnotation.value();
                        setBeanDefinition(className, classFullName, clazz, beanName);
                    }
                } else {
                    System.out.println("这不是一个LHSpring bean=" + clazz + "  类名=" + className);
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }
    }

    /**
     * 设置Bean定义信息。
     *
     * @param className     类名（不包含包名）
     * @param classFullName 完整类名（包含包名）
     * @param clazz         类的Class对象
     * @param beanName      Bean的名称。如果为空，则自动生成，规则为类名首字母小写。
     */
    private void setBeanDefinition(String className, String classFullName, Class<?> clazz, String beanName) {
        if ("".equals(beanName)) {
            // 自动生成beanName，首字母小写
            beanName = className.substring(0, 1).toLowerCase() + className.substring(1);
        }

        // 创建BeanDefinition并设置ID和类名
        BeanDefinition beanDefinition = new BeanDefinition();
        beanDefinition.setId(beanName);
        beanDefinition.setClassName(classFullName);

        // 设置Scope，优先使用类上的Scope注解，若无则默认为singleton
        if (clazz.isAnnotationPresent(Scope.class)) {
            // 读取Scope注解的值
            Scope scopedeclaredAnnotation = clazz.getDeclaredAnnotation(Scope.class);
            beanDefinition.setScope(scopedeclaredAnnotation.value());
        } else {
            // 默认Scope
            beanDefinition.setScope("singleton");
        }
        beanDefinitonMap.put(beanName, beanDefinition);
    }


    /**
     * 获取扫描基本包路径
     *
     * @param configClass 配置类
     */
    private void getScanBasePackagePath(Class<?> configClass) {
        // 异常处理和空检查增强
        try {
            Annotation componentScanAnnotation = configClass.getAnnotation(ComponentScan.class);
            if (componentScanAnnotation != null) {
                ComponentScan componentScan = (ComponentScan) componentScanAnnotation;
                String[] basePackages = componentScan.value();
                if (basePackages.length > 0) {
                    // 设置包路径
                    basePackagePaths = basePackages;
                } else {
                    throw new IllegalArgumentException("ComponentScan注解中的value不能为空");
                }
            } else {
                throw new IllegalArgumentException("配置类上缺少ComponentScan注解");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 得到bean
     *
     * @param beanName bean名字
     * @return {@link Object}
     */
    public Object getBean(String beanName) {
        // 判断 传入的beanName是否在beanDefinitonMap中存在
        if (beanDefinitonMap.containsKey(beanName)) {// 存在
            BeanDefinition beanDefinition = beanDefinitonMap.get(beanName);
            // 得到beanDefinition的scope，分别进行处理
            if ("singleton".equalsIgnoreCase(beanDefinition.getScope())) {
                // 说明是单例的，就直接从单例池获取
                return singletonObjects.get(beanName);
            } else {// 不是单例就调用creatBean，反射一个对象
                return createBean(beanDefinition);
            }
        } else {// 不存在
            throw new NullPointerException("没有该bean");

        }
    }

}
