package lk.ijse.demo.DBProcess;

import lk.ijse.demo.dto.CombinedOrderDTO;
import lk.ijse.demo.dto.OrderDetailsDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class OrderDetailsDB {
    public boolean saveOrderDetails(OrderDetailsDTO orderDetailsDTO, Connection connection) {
        String sql = "INSERT INTO orderdetails (order_id, item_id, price, qty) VALUES (?, ?, ?, ?);";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, orderDetailsDTO.getOrder_id());
            preparedStatement.setString(2, orderDetailsDTO.getItem_id());
            preparedStatement.setDouble(3, orderDetailsDTO.getPrice());
            preparedStatement.setInt(4, orderDetailsDTO.getQty());

            return preparedStatement.executeUpdate() != 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<OrderDetailsDTO> getOrderDetails(String orderId, Connection connection) {
        ArrayList<OrderDetailsDTO> orderDetailsDTOS = new ArrayList<>();

        String sql = "SELECT * FROM orderdetails WHERE order_id = ?;";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,orderId);
            try(ResultSet resultSet = preparedStatement.executeQuery()){
                while (resultSet.next()){
                    OrderDetailsDTO orderDetailsDTO = new OrderDetailsDTO();
                    orderDetailsDTO.setOrder_id(resultSet.getString("order_id"));
                    orderDetailsDTO.setItem_id(resultSet.getString("item_id"));
                    orderDetailsDTO.setPrice(resultSet.getDouble("price"));
                    orderDetailsDTO.setQty(resultSet.getInt("qty"));
                    orderDetailsDTOS.add(orderDetailsDTO);
                }
            }
            return orderDetailsDTOS;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean deleteOrderDetails(String orderId, Connection connection) {
        String sql = "DELETE FROM orderdetails WHERE order_id = ?;";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,orderId);

            return preparedStatement.executeUpdate() !=0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean updateOrderDetails(OrderDetailsDTO orderDetailsDTO, Connection connection) {
        String updateItem = "UPDATE OrderDetails SET price=?, qty=? WHERE order_id=? AND item_id=?;";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(updateItem);
            preparedStatement.setDouble(1, orderDetailsDTO.getPrice());
            preparedStatement.setInt(2, orderDetailsDTO.getQty());
            preparedStatement.setString(3, orderDetailsDTO.getOrder_id());
            preparedStatement.setString(4, orderDetailsDTO.getItem_id());

            return preparedStatement.executeUpdate() != 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
