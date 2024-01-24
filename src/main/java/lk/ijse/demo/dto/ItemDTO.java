package lk.ijse.demo.dto;

import lombok.*;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class ItemDTO implements Serializable {
    private String item_code;
    private String item_name;
    private double price;
    private int qty;

}
