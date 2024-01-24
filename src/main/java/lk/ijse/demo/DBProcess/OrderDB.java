package lk.ijse.demo.DBProcess;

import lk.ijse.demo.dto.CombinedOrderDTO;
import lk.ijse.demo.dto.OrderDTO;
import lk.ijse.demo.dto.OrderDetailsDTO;

import java.sql.*;
import java.util.ArrayList;

public class OrderDB {

    public boolean saveOrder(CombinedOrderDTO combinedOrderDTO, Connection connection) {
        try {
            connection.setAutoCommit(false);
            if (save(combinedOrderDTO.getOrderDTO(), connection)){
                for (OrderDetailsDTO orderDetailsDTO:combinedOrderDTO.getOrderDetailsDTOS()){
                    boolean isSavedOrder = new OrderDetailsDB().saveOrderDetails(orderDetailsDTO, connection);
                    if (isSavedOrder){
                        boolean isSavedItem = new ItemDB().updateItemOrder(orderDetailsDTO, connection);
                        if (!isSavedItem){
                            return false;
                        }
                    }else {
                        return false;
                    }
                }
                connection.commit();
                return false;
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean save(OrderDTO orderDTO, Connection connection) {
        String save_item = "INSERT INTO orders (order_date, order_id, customer_id, total, discount, cash) VALUE  (?,?,?,?,?,?);";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(save_item);
            preparedStatement.setDate(1, Date.valueOf(orderDTO.getOrder_date()));
            preparedStatement.setString(2, orderDTO.getOrder_id());
            preparedStatement.setString(3, orderDTO.getCustomer_id());
            preparedStatement.setDouble(4, orderDTO.getTotal());
            preparedStatement.setDouble(5, orderDTO.getDiscount());
            preparedStatement.setDouble(6, orderDTO.getCash());

            return preparedStatement.executeUpdate() != 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean delete(String orderId, Connection connection) {
        try {
            connection.setAutoCommit(false);
            ArrayList<OrderDetailsDTO> orderDetailsDTOS = new OrderDetailsDB().getOrderDetails(orderId,connection);
            for (OrderDetailsDTO orderDetailsDTO : orderDetailsDTOS){
                orderDetailsDTO.setQty(-orderDetailsDTO.getQty());
                if (!new ItemDB().updateItemOrder(orderDetailsDTO,connection)){
                    return false;
                }
            }
            if (new OrderDetailsDB().deleteOrderDetails(orderId,connection)){
                if (deleteOrder(orderId,connection)){
                    connection.setAutoCommit(true);
                    return true;
                }
            }
            connection.rollback();
            return false;
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }finally {
                try {
                    connection.setAutoCommit(true);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
            throw new RuntimeException(e);
        }
    }

    public boolean deleteOrder(String orderId, Connection connection) {
        String sql = "DELETE FROM orders WHERE order_id = ?;";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,orderId);

            return preparedStatement.executeUpdate() !=0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean updateOrder(CombinedOrderDTO combinedOrderDTO, Connection connection) {
        try {
            connection.setAutoCommit(false);
            ArrayList<OrderDetailsDTO> orderDetailsDTOS = new OrderDetailsDB().getOrderDetails(
                    combinedOrderDTO.getOrderDTO().getOrder_id(),connection
            );

            for (OrderDetailsDTO orderDetailsDTO : combinedOrderDTO.getOrderDetailsDTOS()){
                orderDetailsDTO.setQty(-orderDetailsDTO.getQty());
                if (!new ItemDB().updateItemOrder(orderDetailsDTO,connection)){
                    return false;
                }
            }

            if (update(combinedOrderDTO.getOrderDTO(),connection)){
                for (OrderDetailsDTO orderDetailsDTO : combinedOrderDTO.getOrderDetailsDTOS()){
                    boolean isUpdateOrder = new OrderDetailsDB().updateOrderDetails(orderDetailsDTO,connection);
                    if (isUpdateOrder){
                        boolean isUpdateItem = new ItemDB().updateItemOrder(orderDetailsDTO,connection);
                        if (!isUpdateItem){
                            return false;
                        }
                    }else {
                        return false;
                    }
                }
                connection.commit();
                return true;
            }
            return false;
        } catch (SQLException e) {
            try{
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }finally {
                try {
                    connection.setAutoCommit(true);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
            throw new RuntimeException(e);
        }
    }

    private boolean update(OrderDTO orderDTO, Connection connection) {
        String update_item = "UPDATE Orders SET order_date=?, customer_id=?, total=?, discount=?, cash=? WHERE order_id=?;";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(update_item);
            preparedStatement.setDate(1, Date.valueOf(orderDTO.getOrder_date()));
            preparedStatement.setString(2, orderDTO.getCustomer_id());
            preparedStatement.setDouble(3, orderDTO.getTotal());
            preparedStatement.setDouble(4, orderDTO.getDiscount());
            preparedStatement.setDouble(5, orderDTO.getCash());
            preparedStatement.setString(6, orderDTO.getOrder_id());

            return preparedStatement.executeUpdate() != 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String generateOrderId(Connection connection) {
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
