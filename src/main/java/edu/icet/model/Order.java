package edu.icet.model;
import lombok.*;
import java.util.Date;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Order {
    private String orderID;
    private Date orderDate;
    private String customerId;
    private List<OrderDetail> orderDetailList;
}
