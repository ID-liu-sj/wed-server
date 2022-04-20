package com.webserver.core;


import com.webserver.annotation.Controller;
import com.webserver.annotation.RequestMapping;


import java.io.File;
import java.lang.reflect.Method;

import java.util.HashMap;
import java.util.Map;

/**
 * 该类维护所有请求与对应的业务处理类的关系
 */
public class HandlerMapping {
    /*
    key:请求路径
    value:处理该请求的Controller和对应的业务方法
     */
    private static Map<String, MethodMapping> mapping = new HashMap<>();

    static {
        initMapping();
    }

    private static void initMapping() {
        try {
            File fileDir = new File(HandlerMapping.class.getClassLoader().getResource("./com/webserver/controller").toURI());
            File[] files = fileDir.listFiles(f -> f.getName().endsWith(".class"));
            for (File file : files) {
                Class cls = Class.forName("com.webserver.controller." + file.getName().substring(0, file.getName().indexOf(".")));//第一次是因为"com.webserver.controller[.]"最后一个点没有
                if (cls.isAnnotationPresent(Controller.class)) {
                    Object obj = cls.newInstance();
                    Method[] methods = cls.getMethods();
                    for (Method method : methods) {
                        if (method.isAnnotationPresent(RequestMapping.class)) {
                            RequestMapping rm = method.getAnnotation(RequestMapping.class);
                            String value = rm.value();
                            MethodMapping methodMapping = new MethodMapping(obj, method);
                            mapping.put(value, methodMapping);

                        }
                    }
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        System.out.println(mapping);
        MethodMapping mm = mapping.get("/myweb/reg");
        System.out.println(mm);
    }

    public static MethodMapping getMethod(String path) {
        return mapping.get(path);
    }

    /**
     * 每个实例用于记录一个业务方法以及该方法所属的Controller对象
     */
    public static class MethodMapping {
        private Object controller;
        private Method method;

        public MethodMapping(Object controller, Method method) {
            this.controller = controller;
            this.method = method;
        }

        public Object getController() {
            return controller;
        }


        public Method getMethod() {
            return method;
        }

    }
}

