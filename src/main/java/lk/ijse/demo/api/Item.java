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
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "Item",urlPatterns = "/item")
public class Item extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(Item.class.getName());
    Connection connection;

    @Override
    public void init() throws ServletException {
        try {
            InitialContext initialContext = new InitialContext();
            DataSource lookup = (DataSource) initialContext.lookup("java:comp/env/jdbc/pos_system");
            this.connection = lookup.getConnection();
        } catch (NamingException | SQLException e) {
            LOGGER.log(Level.SEVERE, "Error initializing connection: " + e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LOGGER.info("POST request received");

        if (req.getContentType() != null && req.getContentType().toLowerCase().startsWith("application/json")) {
            Jsonb jsonb = JsonbBuilder.create();
            ItemDTO itemDTO = jsonb.fromJson(req.getReader(), ItemDTO.class);
            var dbProcess = new ItemDB();
            boolean result = dbProcess.saveItem(itemDTO, connection);

            if (result) {
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write("Item Information saved Successfully !");
            } else {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to save Item information !");
            }

        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LOGGER.info("PUT request received");

        if (req.getContentType() != null && req.getContentType().toLowerCase().startsWith("application/json")) {
            Jsonb jsonb = JsonbBuilder.create();
            ItemDTO itemDTO = jsonb.fromJson(req.getReader(), ItemDTO.class);

            var dbProcess = new ItemDB();
            boolean result = dbProcess.updateItem(itemDTO, connection);

            if (result) {
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write("Item information updated successfully.");
            } else {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to update item information.");
            }

        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LOGGER.info("DELETE request received");

        var itemId = req.getParameter("item_code");
        var dbProcess = new ItemDB();
        boolean result = dbProcess.deleteItem(connection, itemId);

        if (result) {
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write("Item information deleted successfully.");
        } else {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to delete item information.");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");

        if (action != null) {
            switch (action) {
                case "getAllItem":
                    LOGGER.info("GET request for all items");
                    getAllItem(req, resp);
                    break;
                case "getItem":
                    String itemId = req.getParameter("itemId");
                    LOGGER.info("GET request for item with ID: " + itemId);
                    getItem(req, resp, itemId);
                    break;
                default:
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                    break;
            }
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private void getItem(HttpServletRequest req, HttpServletResponse resp, String itemId) {
        ItemDB itemDB = new ItemDB();
        ItemDTO itemDTO = itemDB.getItem(itemId, connection);
        Jsonb jsonb = JsonbBuilder.create();
        var json = jsonb.toJson(itemDTO);
        resp.setContentType("application/json");
        try {
            resp.getWriter().write(json);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error writing JSON response: " + e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private void getAllItem(HttpServletRequest req, HttpServletResponse resp) {
        LOGGER.info("Getting all items");
        ItemDB itemDB = new ItemDB();
        ArrayList<ItemDTO> allItem = itemDB.getAllItem(connection);

        Jsonb jsonb = JsonbBuilder.create();
        var json = jsonb.toJson(allItem);
        resp.setContentType("application/json");

        try {
            resp.getWriter().write(json);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error writing JSON response: " + e.getMessage(), e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throw new RuntimeException(e);
        }
    }
}
