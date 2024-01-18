package lk.ijse.demo.api;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebInitParam;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lk.ijse.demo.DBProcess.OrderDB;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

@WebServlet(name = "Order", urlPatterns = "/Order", initParams = {@WebInitParam(name = "db-user", value = "root"),
        @WebInitParam(name = "db-pw", value = "1234"), @WebInitParam(name = "db-url", value = "jdbc:mysql://localhost:3306/pos_system"), @WebInitParam(name = "db-class", value = "com.mysql.cj.jdbc.Driver")}, loadOnStartup = 5

)
public class Order extends HttpServlet {
    Connection connection;

    @Override
    public void init() throws ServletException {
        try {
            InitialContext initialContext = new InitialContext();
            DataSource lookup = (DataSource) initialContext.lookup("java:comp/env/jdbc/pos_system");
            this.connection = lookup.getConnection();
        } catch (NamingException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if (action != null){
            if (action.equals("generateOderID")) {
                generateOderID(req, resp);
            }else if (action.equals(("getAllCustomer"))){
//                getAllCustomer(req,resp);
            }else if (action.equals("getCustomer")){
//                String customerId = req.getParameter("customerId");
//                getCustomer(req, resp, customerId);
            }
        }
    }

    private void generateOderID(HttpServletRequest req, HttpServletResponse resp) {
        System.out.println("triggered");
        var oderDb = new OrderDB();
        String getLastCustomerId = oderDb.generateOderID(connection);

        Jsonb jsonb = JsonbBuilder.create();
        String json = jsonb.toJson(getLastCustomerId);
        resp.setContentType("application/json");
        try {
            resp.getWriter().write(json);
        } catch (IOException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throw new RuntimeException(e);
        }
    }



}
