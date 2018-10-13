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
        UrlExtractor urlExtractor = new UrlExtractor("https://www.houzz.com/professionals/interior-designer/c/New-York--NY");
        List<String> list = urlExtractor.getProjectURLsByPage(0);

    }
}
