package com.DAO;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

/**
 * Date 20.09.2018
 *
 * @author Hursanov Sulaymon
 * @version v1.0
 **/
public class PostgresConnection {

    private static Properties properties;

    private static Connection connection;
    static {
        loadProperties();
    }

    public static Connection connect(){
        try {
            Class.forName(properties.getProperty("driver"));
            String url = properties.getProperty("url");
            String login = properties.getProperty("login");
            String password = properties.getProperty("password");
            connection = DriverManager.getConnection(url,login,password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }

    private static void loadProperties(){
        properties = new Properties();
        InputStream in = PostgresConnection.class.getClassLoader().getResourceAsStream("app.properties");
        try {
            properties.load(in);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
