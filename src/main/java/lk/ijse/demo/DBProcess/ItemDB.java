package lk.ijse.demo.DBProcess;

import lk.ijse.demo.dto.CustomerDTO;
import lk.ijse.demo.dto.ItemDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ItemDB {
    public boolean saveItem(ItemDTO itemDTO, Connection connection) {
        String saveItem = "INSERT INTO item (item_code,item_description,item_qty,item_price) VALUES (?,?,?,?)";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(saveItem);
            preparedStatement.setString(1,itemDTO.getItem_code());
            preparedStatement.setString(2,itemDTO.getItem_description());
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
                        resultSet.getString("item_description"),
                        resultSet.getInt("item_qty"),
                        resultSet.getInt("item_price")
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
                        resultSet.getString("item_description"),
                        resultSet.getInt("item_qty"),
                        resultSet.getInt("item_price")
                );
                itemDTOS.add(itemDTO);
            }
            return itemDTOS;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean updateItem(ItemDTO itemDTO, Connection connection) {
        String updateItem = "UPDATE item SET item_description=?,item_qty=?,item_price=? where  item_code=?;";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(updateItem);
            preparedStatement.setString(1,itemDTO.getItem_description());
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
}
