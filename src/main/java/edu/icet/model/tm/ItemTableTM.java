package edu.icet.model.tm;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ItemTableTM {
    private String itemCode;
    private String description;
    private String packSize;
    private Double unitPrice;
    private int qtyOnHand;
}
