package com.ihouzzScrap;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
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

    public UrlExtractor(String url) {
        this.url = url;
        this.projectEncryptedUrl = "https://www.houzz.com/hsc/pclk/k=";
    }

    // TODO: 14.10.2018 make also with category
    public List<String> getProjectURLsByPage(int page) {
        if (url == null || url.isEmpty()) {
            throw new IllegalArgumentException("Required url!");
        }
        List<String> projectUrls = new ArrayList<>();
        url = url + "/p/" + page;
        try {
            Connection connect = Jsoup.connect(url);
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
                                    if (count%3 == 2) { // getting every 3rd element  3%3=0, 4%3=1
                                        List<String> jsonUrlList = m.getValue();
                                        String url = projectEncryptedUrl + jsonUrlList.get(2) + jsonUrlList.get(1); // getting full encrypted url
                                        Connection.Response response = Jsoup.connect(url)
                                                .timeout(10 * 10000).followRedirects(false).execute();
                                        String locationURL = response.header("location");// get real url when request redirecting
                                        projectUrls.add(locationURL);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return projectUrls;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}

