package com.DAO;

import com.models.HouzzDataModel;
import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.List;

public class HouzzDataDAO {
    private Connection connection;
    private CopyManager copyManager;

    public HouzzDataDAO(Connection connection) {
        this.connection = connection;
        try {
            copyManager = new CopyManager((BaseConnection) connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void save(List<HouzzDataModel> dataList) {
        String SQL = "insert into houzz_tmp(houzz_link_id, project_website, phone, contact, location) VALUES(?,?,?,?,?) ";
        try (PreparedStatement statement = connection.prepareStatement(SQL)) {

            int i = 0;

            for (HouzzDataModel model : dataList) {
                statement.setLong(1, model.getHouzzLinkId());
                statement.setString(2, model.getProjectWebsite());
                statement.setString(3, model.getPhone());
                statement.setString(4, model.getContact());
                statement.setString(5, model.getLocation());

                statement.addBatch();

                if (i % 1000 == 0 || i == dataList.size()) {
                    statement.executeBatch(); // Execute every 1000 items
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void moveToStableTable(Long linkID){
        try {
            String SQL = "insert into houzz(project_website, phone, contact, location, houzz_link_id)  (select distinct on (project_website) project_website, phone, contact, location, houzz_link_id from houzz_tmp); ";
            connection.prepareStatement(SQL).execute();
            connection.prepareStatement("truncate table houzz_tmp").execute();
            dumpToFile(linkID);
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    private void dumpToFile(Long linkID) throws IOException, SQLException {
        String SQL_COPY="copy (Select * From houzz where houzz_link_id="+linkID+" ) To stdout With CSV";
        FileWriter fileWriter = new FileWriter("csv/"+linkID+".csv");
        copyManager.copyOut(SQL_COPY, fileWriter);
        fileWriter.close();
    }
}