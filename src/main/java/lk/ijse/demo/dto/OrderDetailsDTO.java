package lk.ijse.demo.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@ToString
@Getter
public class OrderDetailsDTO {
    private String order_id;
    private String item_id;
    private double price;
    private int qty;
}
