package lk.ijse.demo.api;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebInitParam;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lk.ijse.demo.DBProcess.CustomerDB;
import lk.ijse.demo.dto.CustomerDTO;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

@WebServlet(name = "Customer",urlPatterns = "/customer",initParams = {
        @WebInitParam(name = "db-user",value = "root"),
        @WebInitParam(name = "db-pw",value = "1234"),
        @WebInitParam(name = "db-url",value = "jdbc:mysql://localhost:3306/pos_system"),
        @WebInitParam(name = "db-class",value = "com.mysql.cj.jdbc.Driver")
}
        ,loadOnStartup = 5

)
public class Customer extends HttpServlet {

    Connection connection;

    @Override
    public void init() throws ServletException {
        try{
            InitialContext initialContext = new InitialContext();
            DataSource lookup = (DataSource) initialContext.lookup("java:comp/env/jdbc/pos_system");
            this.connection = lookup.getConnection();
        } catch (NamingException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("POST");
        System.out.println(req.getContentType());
        if (req.getContentType() != null && req.getContentType().toLowerCase().startsWith("application/json")){
            Jsonb jsonb = JsonbBuilder.create();
            CustomerDTO customerDTO = jsonb.fromJson(req.getReader(),CustomerDTO.class);
            var dbProcess = new CustomerDB();
                boolean result = dbProcess.saveCustomer(customerDTO, connection);
                if (result){
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().write("Customer Information saved Successfully !");
                }else {
                    resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"Fail to saved customer information !");
                }

        }else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getContentType() != null && req.getContentType().toLowerCase().startsWith("application/json")){
            Jsonb jsonb = JsonbBuilder.create();
            CustomerDTO customerDTO = jsonb.fromJson(req.getReader(), CustomerDTO.class);

            var dbProcess = new CustomerDB();
            boolean result = dbProcess.updateCustomer(customerDTO, connection);

            if (result){
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write("Customer information updated successfully.");
            }else {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"Failed to update customer information.");
            }

        }else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        var customerId = req.getParameter("customerIdValue");
        var dbProcess = new CustomerDB();
        boolean result = dbProcess.deleteCustomer(connection, customerId);
        if (result) {
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write("Student information delete successfully.");
        } else {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to delete student information.");

        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if (action.equals("getAllCustomer")){
            getAllCustomer(req,resp);
        }else if(action.equals("getCustomer")){
            String customerId = req.getParameter("customerId");
            getCustomer(req,resp,customerId);
        }
    }

    private void getCustomer(HttpServletRequest req, HttpServletResponse resp, String customerId) {
        CustomerDB customerDB = new CustomerDB();
        CustomerDTO customerDTO = customerDB.getCustomer(customerId,connection);
        Jsonb jsonb = JsonbBuilder.create();
        var json = jsonb.toJson(customerDTO);
        resp.setContentType("application/json");
        try {
            resp.getWriter().write(json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void getAllCustomer(HttpServletRequest req, HttpServletResponse resp){
        CustomerDB customerDB = new CustomerDB();
        ArrayList<CustomerDTO> allCustomer = customerDB.getAllCustomer(connection);
        System.out.println(allCustomer==null);
        Jsonb jsonb = JsonbBuilder.create();
        var json = jsonb.toJson(allCustomer);
        resp.setContentType("application/json");
        try {
            resp.getWriter().write(json);
        } catch (IOException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throw new RuntimeException(e);
        }
    }
}
