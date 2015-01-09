package com.cgz.capa.services;

import com.cgz.capa.exceptions.ServiceErrorException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by czarek on 05/01/15.
 *
 */
public class ApplicationDescriptionParserService {
    //grep command equivalents:
    //grep -oh store/apps/developer?id=[^\"\&]* pageString
    //grep -oh store/apps/details?id=[^\"\&]* pageString
    //TODO once fuckedup by new google play store layout, go for simple grepping html page
    //TODO consistent naming conventions

    private static final String APP_DETAILS_PATH = "/store/apps/details?id=";
    private static final String DEVELOPER_DETAILS_PATH = "/store/apps/developer?id=";
    private static final String DIV_WITH_APPLINK_SELECTOR = "div.details";
    private static final String LINK_TO_DEVELOPER_SELECTOR = "a.document-subtitle.primary";
    private static final String LINK_TO_SIMILAR_APP_SELECTOR = "a.title";//"a[href*='store\\/apps\\/details?id='][class='title']";
    private static final String LINK_TO_SIMILAR_APP_DEVELOPER = "a.subtitle";//"a[href*='store\\/apps\\/developer?id='][class='subtitle']";

    private String googlePlayStoreUrl;

    public ApplicationDescriptionParserService(String googlePlayStoreUrl){
        this.googlePlayStoreUrl = googlePlayStoreUrl;
    }


    public  List<String> getSimilarAppsPackageNames(String appPacketName) throws ServiceErrorException {
        String url = prepareAppUrlInStore(appPacketName);
        try {
            Document doc = Jsoup.connect(url).get();
            String developerName = extractDeveloperName(doc);

            Elements divsContainingAppLinks = doc.select(DIV_WITH_APPLINK_SELECTOR);
            List<String> similarAppsList = new ArrayList<>(divsContainingAppLinks.size());
            for (Element div :divsContainingAppLinks) {
                String similarAppName = extractAppNameIfDifferentDeveloper(div, developerName);
                if(similarAppName!=null) {
                    similarAppsList.add(similarAppName);
                }
            }
            return similarAppsList;
        } catch (IOException e) {
            throw new ServiceErrorException(e);
        }
    }

    private String extractDeveloperName(Document doc) throws ServiceErrorException {
        Elements developerNode = doc.select(LINK_TO_DEVELOPER_SELECTOR);
        if(developerNode == null || developerNode.size() == 0) {
            throw new ServiceErrorException("cannot get original app developer name");
        }

        Element linkToDeveloper = developerNode.first();
        if(linkToDeveloper == null || !linkToDeveloper.hasAttr("href")){
            throw new ServiceErrorException("cannot get original app developer name");
        }

        String hrefToDeveloper = linkToDeveloper.attr("href");
        if(hrefToDeveloper== null || hrefToDeveloper.length() < DEVELOPER_DETAILS_PATH.length()){
            throw new ServiceErrorException("cannot get original app developer name");
        }

        return hrefToDeveloper.substring(DEVELOPER_DETAILS_PATH.length() , hrefToDeveloper.length());
    }

    private String extractAppNameIfDifferentDeveloper(Element div, String originalAppDeveloperName) {

        Elements appLinkNode = div.select(LINK_TO_SIMILAR_APP_SELECTOR);
        if(appLinkNode == null || appLinkNode.size() == 0) {
            return null;
        }

        Element appLinkElement = appLinkNode.first();
        if(appLinkElement == null || !appLinkElement.hasAttr("href")){
            return null;
        }
        String appLink = appLinkElement.attr("href");

        Elements appDeveloperNode = div.select(LINK_TO_SIMILAR_APP_DEVELOPER);
        if(appDeveloperNode == null || appDeveloperNode.size() == 0) {
            return null;
        }

        Element appDeveloperLinkElement = appDeveloperNode.first();
        if(appLink == null || !appDeveloperLinkElement.hasAttr("href")){
            return null;
        }

        String appDeveloperLink = appDeveloperLinkElement.attr("href");
        if(appDeveloperLink== null || appDeveloperLink.length() < DEVELOPER_DETAILS_PATH.length()){
            return null;
        }

        String appDeveloper = appDeveloperLink.substring(DEVELOPER_DETAILS_PATH.length() , appDeveloperLink.length() );;

        if(appDeveloper.equals(originalAppDeveloperName)) {
            return null;
        }
        return  appLink.substring(APP_DETAILS_PATH.length(), appLink.length());
    }

    private String prepareAppUrlInStore(String appPacketName) {
            return googlePlayStoreUrl+APP_DETAILS_PATH+appPacketName;
    }
}
