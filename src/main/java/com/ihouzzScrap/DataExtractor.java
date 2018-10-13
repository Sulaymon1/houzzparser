package com.ihouzzScrap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Date 14.10.2018
 *
 * @author Hursanov Sulaymon
 * @version v1.0
 **/
public class DataExtractor {


    private ProjectDTO extractData(String url){
        String houzzUrl = url;
        String website = null;
        String phone = null;
        String contact = null;
        String location = null;
        ProjectDTO projectDTO;
        try {
            Document document = Jsoup.connect(url).get();
            website = document.getElementsByClass("pro-contact-website-text").parents().first().attr("href");
            String profileName = document.getElementsByClass("profile-full-name").text();
            Elements phoneNumber = document.getElementsByClass("pro-contact-text");
            if (phoneNumber != null && phoneNumber.first() != null && phoneNumber.first().firstElementSibling() != null ){
                phone = phoneNumber.first().child(0).attr("phone");
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
            projectDTO = ProjectDTO.builder()
                    .houzzUrl(houzzUrl)
                    .projectWebsite(website)
                    .phone(phone)
                    .contact(contact)
                    .location(location)
                    .build();
            return projectDTO;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<ProjectDTO> extractDataOfAllUrl(List<String> urls){
        List<ProjectDTO> projectDTOList = new ArrayList<>();
        for (String url:urls) {
            ProjectDTO projectDTO = extractData(url);
            if (projectDTO != null){
                projectDTOList.add(projectDTO);
            }
        }
        return projectDTOList;
    }
}
