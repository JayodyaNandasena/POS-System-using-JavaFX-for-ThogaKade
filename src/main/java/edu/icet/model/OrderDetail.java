package edu.icet.model;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class OrderDetail {
    private String itemCode;
    private String itemName;
    private Integer orderQuantity;
    private Double discount;
}
