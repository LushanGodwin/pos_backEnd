Create DATABASE pos_system;

Use pos_system;

CREATE TABLE Customer (
                          customerId VARCHAR(10) PRIMARY KEY,
                          name VARCHAR(255),
                          address VARCHAR(255),
                          contact VARCHAR(20)
);

CREATE TABLE Item (
                      item_code VARCHAR(50) PRIMARY KEY,
                      item_name VARCHAR(255),
                      price DOUBLE,
                      qty INT
);

CREATE TABLE Orders (
                        order_date DATE,
                        order_id VARCHAR(50) PRIMARY KEY,
                        customer_id VARCHAR(50),
                        total DOUBLE,
                        discount DOUBLE,
                        cash DOUBLE,
                        FOREIGN KEY (customer_id) REFERENCES Customer(customerId)
);

CREATE TABLE OrderDetails (
                              order_id VARCHAR(50),
                              item_id VARCHAR(50),
                              price DOUBLE,
                              qty INT,
                              PRIMARY KEY (order_id, item_id),
                              FOREIGN KEY (order_id) REFERENCES Orders(order_id),
                              FOREIGN KEY (item_id) REFERENCES Item(item_code)
);
