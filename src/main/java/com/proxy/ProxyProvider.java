package com.proxy;

import java.io.*;
import java.net.*;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Date 25.10.2018
 *
 * @author Hursanov Sulaymon
 * @version v1.0
 **/
public class ProxyProvider {

    private BufferedReader in;
    private String host;
    private int port;
    private Proxy proxy;

    public ProxyProvider() throws FileNotFoundException {
        readFile();
        changeProxy();
    }

    private void readFile() throws FileNotFoundException {
        String resource = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        File file = new File(Paths.get(resource).getParent().toString()+"/proxy.txt");
        if (!file.exists()){
            throw new FileNotFoundException("proxy file not found!");
        }
        in = new BufferedReader(new FileReader(file));

    }

    private String getProxyAddressFromFile(){
        try {
            String address = in.readLine();
            if (address==null){
                in.close();
                try {
                    Thread.sleep(1000*60*60);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                readFile();
                address = in.readLine();
            }
            return address;
        } catch (IOException e) {
            e.printStackTrace();
            try {
                readFile();
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            }
            return getProxyAddressFromFile();
        }
    }

    public void changeProxy()  {
        String address = getProxyAddressFromFile();
        host = address.split(":")[0];
        port = Integer.parseInt(address.split(":")[1]);
        proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host, port)); // or whatever your proxy is

        try {
            URL url = new URL("https://www.houzz.com/professionals/interior-designer/c/New-York--NY");
            HttpURLConnection uc = (HttpURLConnection) url.openConnection(proxy);
            uc.setReadTimeout(2000);
            uc.setConnectTimeout(5000);
            uc.connect();

            System.out.println("connected");

            if (uc.getResponseCode() != 200) {
                System.out.println("response is: "+uc.getResponseCode());
                changeProxy();
            }

            String page = null;
            int count = 0;
            String line = null;
            BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
            while ((line = in.readLine()) != null) {
                page = line + "\n";
                if (count==4){
                    break;
                }
                count++;
            }
            if (page == null) {
                System.out.println("page null");
                changeProxy();
            }
            System.out.println("changed proxy -> "+address);
        }catch (SocketTimeoutException e){
            System.out.println("connection timeout was thrown exception");
            changeProxy();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public Proxy getProxy(){
        return proxy;
    }


}
