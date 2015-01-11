package com.cgz.capa;

import com.cgz.capa.logic.services.GooglePlayCrawlerService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class HelloApp {
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-config.xml");
        GooglePlayCrawlerService helloService = context.getBean(GooglePlayCrawlerService.class);
    }
}
