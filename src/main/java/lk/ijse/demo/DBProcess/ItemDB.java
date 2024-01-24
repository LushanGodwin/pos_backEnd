package lk.ijse.demo.DBProcess;

import lk.ijse.demo.dto.CustomerDTO;
import lk.ijse.demo.dto.ItemDTO;
import lk.ijse.demo.dto.OrderDetailsDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ItemDB {
    public boolean saveItem(ItemDTO itemDTO, Connection connection) {
        String saveItem = "INSERT INTO item (item_code,item_name,price,qty) VALUES (?,?,?,?)";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(saveItem);
            preparedStatement.setString(1,itemDTO.getItem_code());
            preparedStatement.setString(2,itemDTO.getItem_name());
            preparedStatement.setString(3, String.valueOf(itemDTO.getItem_qty()));
            preparedStatement.setString(4, String.valueOf(itemDTO.getItem_price()));

            return preparedStatement.executeUpdate()!=0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ItemDTO getItem(String itemId, Connection connection) {
        String sql = "select * from item WHERE item_code=?;";
        try {
            PreparedStatement preparedStatement=connection.prepareStatement(sql);
            preparedStatement.setString(1,itemId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                return new ItemDTO(
                        resultSet.getString("item_code"),
                        resultSet.getString("item_name"),
                        resultSet.getInt("item_price"),
                        resultSet.getInt("item_qty")

                );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public ArrayList<ItemDTO> getAllItem(Connection connection) {
        String sql = "select * from item;";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            ArrayList<ItemDTO> itemDTOS = new ArrayList<>();
            while (resultSet.next()){
                ItemDTO itemDTO = new ItemDTO(
                        resultSet.getString("item_code"),
                        resultSet.getString("item_name"),
                        resultSet.getInt("item_price"),
                        resultSet.getInt("item_qty")

                );
                itemDTOS.add(itemDTO);
            }
            return itemDTOS;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean updateItem(ItemDTO itemDTO, Connection connection) {
        String updateItem = "UPDATE item SET item_name=?,price=?,qty=? where  item_code=?;";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(updateItem);
            preparedStatement.setString(1,itemDTO.getItem_name());
            preparedStatement.setString(2, String.valueOf(itemDTO.getItem_qty()));
            preparedStatement.setString(3, String.valueOf(itemDTO.getItem_price()));
            preparedStatement.setString(4,itemDTO.getItem_code());

            return preparedStatement.executeUpdate()!=0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean deleteItem(Connection connection, String itemId) {
        String sql = "delete from item where item_code=?;";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,itemId);

            return preparedStatement.executeUpdate() != 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);

        }
    }

    public boolean updateItemOrder(OrderDetailsDTO orderDetailsDTO, Connection connection) {
        String sql = "UPDATE item SET qty = qty - ? WHERE item_code = ?;";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1,orderDetailsDTO.getQty());
            preparedStatement.setString(2, orderDetailsDTO.getItem_id());

            return preparedStatement.executeUpdate() != 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
