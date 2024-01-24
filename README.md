# Java POS System API Collection

## Introduction
This Postman collection contains a set of API endpoints for interacting with a Java Point of Sale (POS) System. It includes functionality related to customers, items, and orders.

## Getting Started
Follow the steps below to use the APIs in this collection:

1. **Clone the Repository:** Clone the repository containing the Java POS System code.

2. **Set Up the Environment:**
   - Open Postman.
   - Import this collection in postman : [Java POS Collection](https://www.postman.com/supply-physicist-73840039/workspace/java-pos/collection/30946389-9ab2f581-2fd4-42f8-80c2-a58df65d2711?action=share&creator=32541800).
   - Create a new environment and configure the required variables (e.g., base URL).

3. **Run the Requests:**
   - Open the desired request.
   - Ensure that the required parameters are set in the request body or as query parameters.
   - Click "Send" to execute the request.

## Collection Structure
- **Customers:**
  - `GET /customer?action=getAllCustomer`: Get all customers.
  - `GET /customer?action=getCustomer&customerId={customerId}`: Get customer details by ID.
  - `POST /customer`: Create a new customer.
  - `PUT /customer`: Update customer information.
  - `DELETE /customer?customerIdValue={customerId}`: Delete a customer.

- **Items:**
  - `POST /item`: Create a new item.
  - `PUT /item`: Update item information.
  - `DELETE /item?item_code={itemCode}`: Delete an item.
  - `GET /item?action=getAllItem`: Get all items.
  - `GET /item?action=getItem&itemId={itemId}`: Get item details by ID.

- **Orders:**
  - `POST /order`: Create a new order.
  - `PUT /order`: Update order information.
  - `DELETE /order?order_id={orderId}`: Delete an order.
  - `GET /order?action=generateOderID`: Generate a new order ID.
  - `GET /order?action=getAllCustomerId`: Get all customer IDs.

## Additional Notes
- Ensure that you have the necessary permissions to perform certain actions.
- Check the request bodies for required parameters and formats.
- Review the response status codes for success or failure.

Feel free to explore and test the API endpoints using this Postman collection.

## Database Connection Configuration

<Resource name="jdbc/pos_system" auth="Container" type="javax.sql.DataSource"
    maxTotal="10" maxIdle="8" maxWaitMillis="-1"
    username="your user name" password="your password" driverClassName="com.mysql.cj.jdbc.Driver"
    url="jdbc:mysql://localhost:3306/pos_system"/>


Happy coding!
