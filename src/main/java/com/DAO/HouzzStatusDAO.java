package com.DAO;


import com.models.HouzzStatus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class HouzzStatusDAO {

    private Connection connection;
    private Boolean firstTime;
    private HouzzDataDAO houzzDataDAO;

    public HouzzStatusDAO(Connection connection) {
        this.connection = connection;
        houzzDataDAO = new HouzzDataDAO(connection);
        firstTime = true;
    }

    public HouzzStatus nextHouzzUrl() {
        String SQL;
        if (firstTime){
            firstTime = false;
            SQL = "select statusTable.*, linksTable.link " +
                    "from houzz_status as statusTable join houzz_links linksTable on statusTable.houzz_link_id = linksTable.id " +
                    "where is_in_progress is true and status<100 limit 1";
        }else {
            SQL = "select statusTable.*, linksTable.link " +
                    "from houzz_status as statusTable join houzz_links linksTable on statusTable.houzz_link_id = linksTable.id " +
                    "where is_in_progress is false and status<100 limit 1";
        }

        HouzzStatus houzzStatus = null;
        try (PreparedStatement statement = connection.prepareStatement(SQL)) {
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                long linkId = resultSet.getLong("houzz_link_id");
                String link = resultSet.getString("link");
                Boolean isInProgress = resultSet.getBoolean("is_in_progress");
                int onPage = resultSet.getInt("on_page");
                int status = resultSet.getInt("status");
                houzzStatus = new HouzzStatus();
                houzzStatus.setHouzzLink(link);
                houzzStatus.setInProgress(isInProgress);
                houzzStatus.setOn_page(onPage);
                houzzStatus.setStatus(status);
                houzzStatus.setHouzzId(linkId);

                update(linkId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return houzzStatus;
    }


    private void update(Long linkId) throws SQLException {
        PreparedStatement preparedStatement =
                connection.prepareStatement("UPDATE houzz_status SET is_in_progress=TRUE WHERE houzz_link_id=?");
        preparedStatement.setLong(1,linkId);
        preparedStatement.execute();
    }

    public void updateStatus(int status, int onPage, Long linkID){
        try {
            PreparedStatement preparedStatement =
                    connection.prepareStatement("update houzz_status set status=?, on_page=? where houzz_link_id=?");
            preparedStatement.setInt(1, status);
            preparedStatement.setInt(2, onPage);
            preparedStatement.setLong(3 , linkID);
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void finish(Long linkID){
        String SQL="UPDATE houzz_status SET is_in_progress=FALSE, status=100 WHERE houzz_link_id=?";
        try {
            houzzDataDAO.moveToStableTable(linkID);
            PreparedStatement statement = connection.prepareStatement(SQL);
            statement.setLong(1, linkID);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
