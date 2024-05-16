package edu.icet.controller.item;

import edu.icet.model.Customer;
import edu.icet.model.Item;
import edu.icet.model.OrderDetail;
import javafx.collections.ObservableList;

import java.util.List;

public interface ItemService {
    boolean addItem(Item item);
    boolean updateItem(Item item);
    Item searchItem(String itemCode);
    boolean deleteItem(String itemCode);
    ObservableList<Item> getAllItems();
    Boolean updateStock(List<OrderDetail> orderDetailList);
}
