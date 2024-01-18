package lk.ijse.demo.DBProcess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderDB {
    public String generateOderID(Connection connection) {
        String get_oder_item_id = "SELECT MAX(order_id) as last_oder_id FROM oder;";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(get_oder_item_id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String lastItemId = resultSet.getString("last_oder_id");
                if (lastItemId == null) {
                    return "OP-0001";
                } else {
                    int nextId = Integer.parseInt(lastItemId.substring(5)) + 1;
                    return "OP-" + String.format("%04d", nextId);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
