package edu.icet.controller.item;

import edu.icet.crudUtil.CrudUtil;
import edu.icet.db.DBConnection;
import edu.icet.model.Item;
import edu.icet.model.OrderDetail;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ItemController implements ItemService {
    private static ItemController instance;

    private ItemController(){}

    public static ItemController getInstance(){
        if (instance==null){
            instance=new ItemController();
        }
        return instance;
    }
    @Override
    public ObservableList<Item> getAllItems(){
        try {
            ResultSet resultSet = CrudUtil.execute("SELECT * FROM item");

            ObservableList<Item> itemObservableList= FXCollections.observableArrayList();

            while (resultSet.next()) {
                itemObservableList.add(new Item(
                        resultSet.getString(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getDouble(4),
                        resultSet.getInt(5)
                        )
                );
            }
            return itemObservableList;
        } catch (SQLException | ClassNotFoundException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
        return null;
    }

    @Override
    public Boolean updateStock(List<OrderDetail> orderDetailList) {
        try {
            for (OrderDetail orderDetail:orderDetailList){
                Boolean isUpdated=CrudUtil.execute("UPDATE item SET QtyOnHand=QtyOnHand-? WHERE ItemCode = ?",orderDetail.getItemQuantity(),orderDetail.getItemCode());
                if (!isUpdated){
                    return false;
                }
            }
            return true;
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean addItem(Item item) {
        String sql="INSERT INTO item VALUES (?,?,?,?,?)";
        try {
            return CrudUtil.execute(sql,
                    item.getItemCode(),
                    item.getDescription(),
                    item.getPackSize(),
                    item.getUnitPrice(),
                    item.getQtyOnHand()
            );
        } catch (SQLException | ClassNotFoundException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
        return false;
    }

    @Override
    public boolean updateItem(Item item) {
        String sql = "UPDATE item SET " +
                "ItemCode='" + item.getItemCode() + "'," +
                "Description='" + item.getDescription() + "'," +
                "PackSize='" + item.getPackSize() + "'," +
                "UnitPrice='" + item.getUnitPrice() + "'," +
                "QtyOnHand='" + item.getQtyOnHand() + "'" +
                "WHERE ItemCode='"+item.getItemCode()+"'";
        try {
            return CrudUtil.execute(sql);
        } catch (SQLException | ClassNotFoundException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(e.getMessage());
            alert.show();
        }
        return false;
    }

    @Override
    public Item searchItem(String itemCode){
        try {
            String sql="SELECT * FROM item WHERE ItemCode=?";
            ResultSet resultSet = CrudUtil.execute(sql, itemCode);

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
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
        return null;
    }

    @Override
    public boolean deleteItem(String itemCode) {
        try {
            return CrudUtil.execute("DELETE FROM item WHERE ItemCode=?",itemCode);
        } catch (SQLException | ClassNotFoundException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
        return false;
    }
}
