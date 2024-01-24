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
import lk.ijse.demo.DBProcess.OrderDB;
import lk.ijse.demo.dto.CombinedOrderDTO;
import lk.ijse.demo.dto.CustomerDTO;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "order", urlPatterns = "/order")
public class Order extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(Order.class.getName());
    Connection connection;

    @Override
    public void init() throws ServletException {
        LOGGER.info("Initializing OrderServlet");
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
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");

        if (action != null) {
            if (action.equals("generateOrderId")) {
                LOGGER.info("GET request for generating order ID");
                generateOrderId(req, resp);
            } else if (action.equals("getAllCustomerIds")) {
                LOGGER.info("GET request for getting all customer IDs");
                getAllCustomerIds(req, resp);
            }
        }
    }

    private void generateOrderId(HttpServletRequest req, HttpServletResponse resp) {
        LOGGER.info("Generating Order ID");

        var orderDb = new OrderDB();
        String lastOrderId = orderDb.generateOrderId(connection);

        Jsonb jsonb = JsonbBuilder.create();
        String json = jsonb.toJson(lastOrderId);
        resp.setContentType("application/json");

        try {
            resp.getWriter().write(json);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error writing JSON response: " + e.getMessage(), e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throw new RuntimeException(e);
        }
    }

    private void getAllCustomerIds(HttpServletRequest req, HttpServletResponse resp) {
        LOGGER.info("Getting all Customer IDs");

        var customerDb = new CustomerDB();
        ArrayList<String> allCustomerIds = customerDb.getAllCustomerIds(connection);

        Jsonb jsonb = JsonbBuilder.create();
        var json = jsonb.toJson(allCustomerIds);
        resp.setContentType("application/json");

        try {
            resp.getWriter().write(json);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error writing JSON response: " + e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LOGGER.info("POST request received");

        if (req.getContentType() != null && req.getContentType().toLowerCase().startsWith("application/json")) {
            Jsonb jsonb = JsonbBuilder.create();
            CombinedOrderDTO combinedOrderDTO = jsonb.fromJson(req.getReader(), CombinedOrderDTO.class);
            OrderDB orderDB = new OrderDB();
            boolean result = orderDB.saveOrder(combinedOrderDTO, connection);

            if (result) {
                resp.setStatus(HttpServletResponse.SC_OK);
                LOGGER.info("Order saved successfully");
            }
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LOGGER.info("DELETE request received");

        String orderId = req.getParameter("order_id");
        if (orderId != null) {
            OrderDB orderDB = new OrderDB();
            boolean result = orderDB.deleteOrder(orderId, connection);

            if (result) {
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write("Order deleted successfully.");
            } else {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to delete order.");
            }
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing orderId parameter.");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LOGGER.info("PUT request received");

        if (req.getContentType() != null && req.getContentType().toLowerCase().startsWith("application/json")) {
            Jsonb jsonb = JsonbBuilder.create();
            CombinedOrderDTO combinedOrderDTO = jsonb.fromJson(req.getReader(), CombinedOrderDTO.class);
            OrderDB orderDB = new OrderDB();
            boolean result = orderDB.updateOrder(combinedOrderDTO, connection);

            if (result) {
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write("Order information updated successfully.");
            } else {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to update order information.");
            }
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
