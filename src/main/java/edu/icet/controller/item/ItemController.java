package edu.icet.controller.item;

import edu.icet.db.DBConnection;
import edu.icet.model.Item;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ItemController {
    private static ItemController instance;

    private ItemController(){}
    public static ItemController getInstance(){
        if (instance==null){
            instance=new ItemController();
        }
        return instance;
    }
    public ObservableList<Item> getAllItems(){
        try {
            ResultSet resultSet = DBConnection.getInstance().getConnection().createStatement().executeQuery("SELECT * FROM item");

            ObservableList<Item> itemObservableList= FXCollections.observableArrayList();

            while (resultSet.next()) {
                Item item = new Item(
                        resultSet.getString(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getDouble(4),
                        resultSet.getInt(5)
                );
                itemObservableList.add(item);
            }
            return itemObservableList;
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public Item searchItem(String itemCode){
        try {
            ResultSet resultSet = DBConnection.getInstance().getConnection().createStatement().executeQuery("SELECT * FROM item WHERE ItemCode='" + itemCode + "'");

            while (resultSet.next()) {
                return new Item(
                        resultSet.getString(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getDouble(4),
                        resultSet.getInt(5)
                );
            }

        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return null;
    }
}
