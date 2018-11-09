package com.ihouzzScrap;

import com.models.HouzzDataModel;
import com.proxy.ProxyProvider;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.*;

/**
 * Date 14.10.2018
 *
 * @author Hursanov Sulaymon
 * @version v1.0
 **/
public class DataExtractor {

    private ProxyProvider proxyProvider;
    private Set<String> uniqueWebsite;

    public DataExtractor(ProxyProvider proxyProvider) {
        uniqueWebsite = new HashSet<>();
        this.proxyProvider = proxyProvider;
    }

    public DataExtractor() {
    }

    private HouzzDataModel extractData(Long urlID,String url){
        String houzzUrl = null;
        String website = null;
        String phone = null;
        String contact = null;
        String location = null;
        HouzzDataModel houzzDataModel;
        Document document = connect(url);
        if (document == null)
            return null;
        houzzUrl = document.location();
        Elements websiteElements = document.getElementsByClass("pro-contact-website-text");
        if (websiteElements != null && websiteElements.parents() != null && websiteElements.parents().first() != null)
            website = websiteElements.parents().first().attr("href");
        String profileName = document.getElementsByClass("profile-full-name").text();
        Elements phoneNumber = document.getElementsByClass("pro-contact-text");
        if (phoneNumber != null && phoneNumber.first() != null){
            try {
                if (phoneNumber.first().text() != null && phoneNumber.first().text().equals("Click to Call"))
                    phone = phoneNumber.first().child(0).attr("phone");
                else
                    phone = phoneNumber.first().text();
            }catch (IndexOutOfBoundsException e){
                e.printStackTrace();
                phone = phoneNumber.first().text();
            }
        }
        Elements contactInfo = document.getElementsByTag("b");
        if (contactInfo != null)
            for (Element info: contactInfo){
                if (info.hasText() && info.text().equals("Contact")) {
                    contact = info.parent().text().substring(8);
                }else if (info.hasText() && info.text().equals("Location")) {
                    location = info.parent().text().substring(9);
                }
            }
        houzzDataModel = new HouzzDataModel();
            houzzDataModel.setContact(contact);
            houzzDataModel.setHouzzLinkId(urlID);
            houzzDataModel.setHouzzUrl(houzzUrl);
            houzzDataModel.setLocation(location);
            houzzDataModel.setPhone(phone);
            houzzDataModel.setProjectWebsite(website);
        return houzzDataModel;
    }

    private Document connect(String url){
        Document document = null;
        try {
            Connection connection;
            if (proxyProvider != null){
                connection = Jsoup.connect(url).proxy(proxyProvider.getProxy()).followRedirects(true);
            }else connection = Jsoup.connect(url).followRedirects(true);
            document = connection.get();
        }catch (HttpStatusException status) {
            if (status.getStatusCode() == 429) {
                proxyProvider.changeProxy();
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                return connect(url);
            }
        }catch (SocketTimeoutException s){
            proxyProvider.changeProxy();
            return null;
        } catch (IOException e) {
           //e.printStackTrace();
           proxyProvider.changeProxy();
           return null;
        }
        return document;
    }

    public List<HouzzDataModel> extractDataOfAllUrl(Long urlID,List<String> urls){
        List<HouzzDataModel> houzzDataModels = new ArrayList<>();
        for (String url:urls) {
            HouzzDataModel houzzDataModel = extractData(urlID,url);
            if (houzzDataModel != null && !uniqueWebsite.contains(houzzDataModel.getProjectWebsite())){
                uniqueWebsite.add(houzzDataModel.getProjectWebsite());
                houzzDataModels.add(houzzDataModel);
            }
        }
        return houzzDataModels;
    }
}
