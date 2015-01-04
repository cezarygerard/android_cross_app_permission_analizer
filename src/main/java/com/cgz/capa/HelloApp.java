package com.cgz.capa;

import com.cgz.capa.services.SystemPermissionsInfoService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class HelloApp {
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-config.xml");
        SystemPermissionsInfoService helloService = context.getBean(SystemPermissionsInfoService.class);
    }
}
