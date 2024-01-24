package lk.ijse.demo.DBProcess;

import lk.ijse.demo.dto.CustomerDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CustomerDB {
    public boolean saveCustomer(CustomerDTO customerDTO, Connection connection){
        String saveCustomer = "INSERT INTO customer (customer_id,customer_name,customer_address,customer_contact)  VALUES(?,?,?,?);";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(saveCustomer);
            preparedStatement.setString(1,customerDTO.getCustomer_id());
            preparedStatement.setString(2,customerDTO.getCustomer_name());
            preparedStatement.setString(3,customerDTO.getCustomer_address());
            preparedStatement.setString(4, String.valueOf(customerDTO.getCustomer_contact()));

            return preparedStatement.executeUpdate()!=0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<CustomerDTO> getAllCustomer(Connection connection){
        String get_all_customer = "select * from customer;";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(get_all_customer);
            ResultSet resultSet = preparedStatement.executeQuery();
            ArrayList<CustomerDTO> customerDTOS = new ArrayList<>();
            while (resultSet.next()){
                CustomerDTO customerDTO = new CustomerDTO(
                        resultSet.getString("customer_id"),
                        resultSet.getString("customer_name"),
                        resultSet.getString("customer_address"),
                        resultSet.getInt("customer_contact")
                );
                customerDTOS.add(customerDTO);
            }
            return customerDTOS;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public CustomerDTO getCustomer(String customerId, Connection connection) {
        String sql = "SELECT * from customer WHERE customer_id= ?;";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,customerId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                return new CustomerDTO(
                        resultSet.getString("customer_id"),
                        resultSet.getString("customer_name"),
                        resultSet.getString("customer_address"),
                        resultSet.getInt("customer_contact")
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    return null;
    }

    public boolean updateCustomer(CustomerDTO customerDTO, Connection connection) {
        String sql = "update customer set customer_name = ?, customer_address =?, customer_contact = ? where customer_id = ?;";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,customerDTO.getCustomer_name());
            preparedStatement.setString(2,customerDTO.getCustomer_address());
            preparedStatement.setString(3, String.valueOf(customerDTO.getCustomer_contact()));
            preparedStatement.setString(4,customerDTO.getCustomer_id());

            return preparedStatement.executeUpdate() != 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);

        }
    }

    public boolean deleteCustomer(Connection connection, String customerId) {
        String sql = "delete from customer where customer_id=?;";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,customerId);

            return preparedStatement.executeUpdate() != 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);

        }
    }

    public ArrayList<String> getAllCustomerIds(Connection connection) {
        String sql = "select customerId from customer;";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            ArrayList<String> allCustomerIds = new ArrayList<>();
            while (resultSet.next()){
                String customerId = resultSet.getString("customerId");
                allCustomerIds.add(customerId);
            }
            return allCustomerIds;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
