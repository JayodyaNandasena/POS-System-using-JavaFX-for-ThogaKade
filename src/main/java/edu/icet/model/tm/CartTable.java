package edu.icet.model.tm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CartTable {
    private String itemCode;
    private String itemName;
    private Integer orderQuantity;
    private Double discount;
    private Double unitPrice;
    private Double price;
}
