package com.ihouzzScrap;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.proxy.ProxyProvider;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.*;

/**
 * Date 13.10.2018
 *
 * @author Hursanov Sulaymon
 * @version v1.0
 **/
public class UrlExtractor {

    private String url;
    private String projectEncryptedUrl;
    private ProxyProvider proxyProvider;

    public UrlExtractor(String url, ProxyProvider proxyProvider) {
        this.url = url;
        this.projectEncryptedUrl = "https://www.houzz.com/hsc/pclk/k=";
        this.proxyProvider = proxyProvider;
        if (url == null || url.isEmpty()) {
            throw new IllegalArgumentException("Required url!");
        }
    }


    public int getPageCount(String url){
        Document document;
        String urlWithPage =url + "/p/6000";
        try {
            if (proxyProvider != null){
                document = Jsoup.connect(urlWithPage).proxy(proxyProvider.getProxy()).get();
            }else {
                    document = Jsoup.connect(urlWithPage).get();
            }
            Elements pageNumberOn = document.getElementsByClass("pageNumberOn");
            if (pageNumberOn != null && pageNumberOn.size()>0){
                Element element = pageNumberOn.get(0);
                int page = Integer.parseInt(element.text());
                System.out.println("found pages: "+page);
                return page;
            }
        } catch (HttpStatusException e) {
            int statusCode = e.getStatusCode();
            if (statusCode == 429){
                proxyProvider.changeProxy();
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                return getPageCount(url);
            }
        }catch (ConnectException | SocketTimeoutException e){
            proxyProvider.changeProxy();
            return getPageCount(url);
        } catch (IOException e){
            e.printStackTrace();
            return 0;
        }
        return 0;

    }

    // TODO: 14.10.2018 make also with category
    public List<String> getProjectURLsByPage(int page) {
        List<String> projectUrls = new ArrayList<>();
        String urlWithPage = url + "/p/" + page;
        try {
            Connection connect;
            if (proxyProvider != null) {
                connect = Jsoup.connect(urlWithPage).proxy(proxyProvider.getProxy());
            } else
                connect = Jsoup.connect(urlWithPage);
            connect.timeout(10 * 1000);
            Document document = connect.get();
            if (document != null) { // check document to not null
                Elements scripts = document.getElementsByAttributeValue("type", "text/javascript"); // get all javascript code part
                if (scripts != null) {
                    for (Element script : scripts) {
                        for (DataNode node : script.dataNodes()) { // iterate every script and check to containing json with url's
                            String wholeData = node.getWholeData();
                            if (wholeData.contains("_h_url_paid_pro = {")) {
                                String jsonUrls = wholeData.substring(22, wholeData.indexOf("};") + 1); // get json of encrypted urls
                                ObjectMapper mapper = new ObjectMapper();
                                mapper.registerModule(new JavaTimeModule());
                                Map<Integer, List<String>> map =
                                        mapper.readValue(jsonUrls, new TypeReference<Map<Integer, List<String>>>() {
                                        }); // mapping json to java object
                                int count = 1;
                                for (Map.Entry<Integer, List<String>> m : map.entrySet()) {
                                    count++;
                                    if (count % 3 == 2) { // getting every 3rd element  3%3=0, 4%3=1
                                        List<String> jsonUrlList = m.getValue();
                                        String url = projectEncryptedUrl + jsonUrlList.get(2) + jsonUrlList.get(1); // getting full encrypted url
                                        projectUrls.add(url);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return projectUrls;
        } catch (HttpStatusException e) {
            if (e.getStatusCode() == 429) {
                proxyProvider.changeProxy();
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                getProjectURLsByPage(page);
            }
            return null;
        }catch (SocketTimeoutException s){
            System.out.println(urlWithPage);
            s.printStackTrace();
            return null;
        }catch (ConnectException ignore){
            return null;
        }
        catch (IOException e) {
            e.printStackTrace();

            return null;
        }

    }

}

