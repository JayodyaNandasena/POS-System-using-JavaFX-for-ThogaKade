package edu.icet.model.tm;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString

public class OrderTableTM {
    private String itemCode;
    private String itemName;
    private Integer orderQuantity;
    private Double discount;
}
