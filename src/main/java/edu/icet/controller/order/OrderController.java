package edu.icet.controller.order;

import com.mysql.cj.x.protobuf.MysqlxCrud;
import edu.icet.controller.item.ItemController;
import edu.icet.db.DBConnection;
import edu.icet.model.Order;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class OrderController {
    private static OrderController instance;

    private OrderController(){}

    public static OrderController getInstance(){
        if (instance == null){
            instance = new OrderController();
        }
        return instance;
    }

    public Boolean placeOrder(Order order) {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            connection.setAutoCommit(false);

            PreparedStatement psTm = connection.prepareStatement("INSERT INTO orders VALUES(?,?,?)");

            psTm.setString(1, order.getOrderID());
            psTm.setObject(2, order.getOrderDate());
            psTm.setString(3, order.getCustomerId());

            boolean isOrderAdded = psTm.executeUpdate() > 0;

            if (isOrderAdded) {
                Boolean isOrderDetailAdded = OrderDetailController.getInstance().addOrderDetail(order.getOrderDetailList());

                if (isOrderDetailAdded) {
                    Boolean isStockUpdated = ItemController.getInstance().updateStock(order.getOrderDetailList());

                    if (isStockUpdated) {
                        connection.setAutoCommit(true);
                        return true;
                    }
                }
            }
            connection.rollback();
            return false;
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }

    }

}
