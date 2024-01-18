package lk.ijse.demo.dto;

import lombok.*;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class CustomerDTO implements Serializable {
    private String customer_id;
    private String customer_name;
    private String customer_address;
    private int customer_contact;
}
