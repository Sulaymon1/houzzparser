package com.ihouzzScrap;

import com.DAO.HouzzDataDAO;
import com.DAO.HouzzStatusDAO;
import com.DAO.PostgresConnection;
import com.models.HouzzDataModel;
import com.models.HouzzStatus;
import com.proxy.ProxyProvider;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.util.List;

/**
 * Date 13.10.2018
 *
 * @author Hursanov Sulaymon
 * @version v1.0
 **/
public class Launcher {

    private static Connection connect = PostgresConnection.connect();
    private static HouzzDataDAO houzzDataDAO = new HouzzDataDAO(connect);
    private static HouzzStatusDAO houzzStatusDAO = new HouzzStatusDAO(connect);
    private static ProxyProvider proxyProvider;

    public static void main(String[] args) throws IOException {
        proxyProvider = new ProxyProvider();
        HouzzStatus houzzStatus = houzzStatusDAO.nextHouzzUrl();
        if (houzzStatus == null){
            houzzStatus = houzzStatusDAO.nextHouzzUrl();
        }
        if (houzzStatus != null){
            parse(houzzStatus);
        }


    }


    private static void parse(HouzzStatus houzzStatus) {

        UrlExtractor urlExtractor = new UrlExtractor(houzzStatus.getHouzzLink(),proxyProvider);
        DataExtractor dataExtractor = new DataExtractor(proxyProvider);


        int pageCount = urlExtractor.getPageCount(houzzStatus.getHouzzLink());
        int page = 0;
        if (houzzStatus.getInProgress()!= null && houzzStatus.getInProgress()){
            page = houzzStatus.getOn_page();
        }
        for (page = 0; page <= pageCount*15; page += 15) {
            List<String> list = urlExtractor.getProjectURLsByPage(page);
            if (list == null)continue;
            List<HouzzDataModel> houzzDataModelList = dataExtractor.extractDataOfAllUrl(houzzStatus.getHouzzId(),list);
            if (houzzDataModelList.size()>0)
            houzzDataDAO.save(houzzDataModelList);
            houzzStatusDAO.updateStatus((int)(((double)page/(double) (pageCount*15))*100), page, houzzStatus.getHouzzId());
            System.out.println("done page:"+page);

        }

        houzzStatusDAO.finish(houzzStatus.getHouzzId());

        notifyServer(houzzStatus);


        HouzzStatus houzzStatus1 = houzzStatusDAO.nextHouzzUrl();
        if (houzzStatus1 != null){
            parse(houzzStatus1);
        }
    }

    private static void notifyServer(HouzzStatus houzzStatus) {
        try {
            String secret = "5EBE2294ECD0E0F08EAB7690D2A6EE69";
            Long urlId = houzzStatus.getHouzzId();
            URL url = new URL("http://localhost:8080/notify?secret="+secret+"&urlID="+urlId);
            HttpURLConnection uc = (HttpURLConnection) url.openConnection();
            uc.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
//        String URL = "https://www.houzz.com/professionals/interior-designer/c/New-York--NY";


/*  zoominfo scrapping
projectDTOList.forEach(projectDTO->{
                String website = projectDTO.getProjectWebsite();
                if (website == null){
                    return;
                }
                if (website.startsWith("http://")){
                    website = website.substring(7);
                } else if (website.startsWith("https://")){
                    website = website.substring(8);
                }
                if (!website.startsWith("www.")){
                    website = "www."+website;
                }
                System.out.print("website: "+website);
                String name = website.split("\\.")[1];
                zoomInfoScrapper.extract(website +" " + name);
                System.out.println();
            });*/
