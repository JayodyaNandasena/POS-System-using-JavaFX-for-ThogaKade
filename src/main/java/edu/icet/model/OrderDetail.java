package edu.icet.model;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class OrderDetail {
    private String orderID;
    private String itemCode;
    private Integer itemQuantity;
    private Double discount;
}
