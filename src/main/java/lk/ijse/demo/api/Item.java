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
import lk.ijse.demo.DBProcess.ItemDB;
import lk.ijse.demo.dto.CustomerDTO;
import lk.ijse.demo.dto.ItemDTO;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

@WebServlet(name = "Item",urlPatterns = "/item",initParams = {
        @WebInitParam(name = "db-user",value = "root"),
        @WebInitParam(name = "db-pw",value = "1234"),
        @WebInitParam(name = "db-url",value = "jdbc:mysql://localhost:3306/pos_system"),
        @WebInitParam(name = "db-class",value = "com.mysql.cj.jdbc.Driver")
}
        ,loadOnStartup = 5

)
public class Item extends HttpServlet {

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
        if (req.getContentType() != null && req.getContentType().toLowerCase().startsWith("application/json")){
            Jsonb jsonb = JsonbBuilder.create();
            ItemDTO itemDTO = jsonb.fromJson(req.getReader(),ItemDTO.class);
            var dbProcess = new ItemDB();
            boolean result = dbProcess.saveItem(itemDTO, connection);
            if (result){
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write("Item Information saved Successfully !");
            }else {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"Fail to saved Item information !");
            }

        }else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getContentType() != null && req.getContentType().toLowerCase().startsWith("application/json")){
            Jsonb jsonb = JsonbBuilder.create();
            ItemDTO itemDTO = jsonb.fromJson(req.getReader(), ItemDTO.class);

            var dbProcess = new ItemDB();
            boolean result = dbProcess.updateItem(itemDTO, connection);

            if (result){
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write("Item information updated successfully.");
            }else {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"Failed to update item information.");
            }

        }else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        var itemId = req.getParameter("item_code");
        var dbProcess = new ItemDB();
        boolean result = dbProcess.deleteItem(connection, itemId);
        if (result) {
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write("Item information delete successfully.");
        } else {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to delete item information.");

        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if (action.equals("getAllItem")){
            getAllItem(req,resp);
        } else if (action.equals("getItem")) {
            String itemId = req.getParameter("itemId");
            getItem(req,resp,itemId);
        }

    }

    private void getItem(HttpServletRequest req, HttpServletResponse resp, String itemId) {
        ItemDB itemDB = new ItemDB();
        ItemDTO itemDTO=itemDB.getItem(itemId,connection);
        Jsonb jsonb = JsonbBuilder.create();
        var json = jsonb.toJson(itemDTO);
        resp.setContentType("application/json");
        try {
            resp.getWriter().write(json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void getAllItem(HttpServletRequest req, HttpServletResponse resp) {
        ItemDB itemDB = new ItemDB();
        ArrayList<ItemDTO> allItem =itemDB.getAllItem(connection);
        Jsonb jsonb = JsonbBuilder.create();
        var json = jsonb.toJson(allItem);
        resp.setContentType("application/json");
        try {
            resp.getWriter().write(json);
        } catch (IOException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throw new RuntimeException(e);
        }
    }
}
