package com.ihouzzScrap;

import java.util.List;

/**
 * Date 13.10.2018
 *
 * @author Hursanov Sulaymon
 * @version v1.0
 **/
public class Launcher {

    public static void main(String[] args) {
        String URL = "https://www.houzz.com/professionals/interior-designer/c/New-York--NY";
        UrlExtractor urlExtractor = new UrlExtractor(URL);
        DataExtractor dataExtractor = new DataExtractor();
        List<String> list = urlExtractor.getProjectURLsByPage(0);
        List<ProjectDTO> projectDTOList = dataExtractor.extractDataOfAllUrl(list);


    }
}
