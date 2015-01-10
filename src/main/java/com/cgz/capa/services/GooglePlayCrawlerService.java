package com.cgz.capa.services;

import com.google.protobuf.ServiceException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.mina.core.IoUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Created by czarek on 05/01/15.
 */
public class GooglePlayCrawlerService {
    //dla danej nazwy pakietowej pobierz listę uprawnien
    //stateless!


    public GooglePlayCrawlerService(String email, String password,  String androidID) throws IOException, ServiceException {
        mustNotBeEmpty(email,password,androidID);
    }

    private void mustNotBeEmpty(String... args) throws ServiceException {
        for (int i = 0; i < args.length; i++) {
            if(StringUtils.isEmpty(args[i])){
                throw new ServiceException("prameter cann not be empty");
            }
            System.out.println(args[i]);
        }
    }
}
