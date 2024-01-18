package lk.ijse.demo.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@ToString
@Getter
public class OrderDTO {
    private String order_id;
    private String customer_id;
    private int order_date;
}
