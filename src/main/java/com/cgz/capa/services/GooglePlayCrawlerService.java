package com.cgz.capa.services;

import org.springframework.stereotype.Service;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * Created by czarek on 05/01/15.
 */
@Service
public class GooglePlayCrawlerService {
    //dla danej nazwy pakietowej pobierz listę uprawnien
    //stateless!

    public GooglePlayCrawlerService(){
        String url = this.getClass().getResource("").getPath();
        System.out.println("!!!!!!!!!!! ----:"+  url);

        ClassLoader cl = ClassLoader.getSystemClassLoader();

        URL[] urls = ((URLClassLoader)cl).getURLs();

        for(URL url2: urls){
            System.out.println(url2.getFile());
        }

    }
}
