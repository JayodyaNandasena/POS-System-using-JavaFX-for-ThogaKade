package edu.icet.controller.order;

import edu.icet.crudUtil.CrudUtil;
import edu.icet.model.OrderDetail;

import java.sql.SQLException;
import java.util.List;

public class OrderDetailController {
    private static OrderDetailController instance;
    private OrderDetailController(){}
    public Boolean addOrderDetail(List<OrderDetail> orderDetailList){
        for (OrderDetail orderDetail:orderDetailList){
            Boolean isAdd = addOrderDetail(orderDetail);

            if (!isAdd){
                return false;
            }
        }
        return true;
    }

//    public Boolean addOrderDetail(List<OrderDetail> orderDetailList){
//        for (OrderDetail orderDetail:orderDetailList){
//            Boolean isAdd = null;
//            try {
//                isAdd = CrudUtil.execute("INSERT INTO orderdetail VALUES(?,?,?,?", orderDetail.getOrderID(), orderDetail.getItemCode(), orderDetail.getItemQuantity(), orderDetail.getDiscount());
//                if (!isAdd){
//                    return false;
//                }
//                return true;
//            } catch (SQLException | ClassNotFoundException e) {
//                throw new RuntimeException(e);
//            }
//        }
//    }

    public Boolean addOrderDetail(OrderDetail orderDetail){
        try {
            Object execute = CrudUtil.execute("INSERT INTO orderdetail VALUES(?,?,?,?)", orderDetail.getOrderID(), orderDetail.getItemCode(), orderDetail.getItemQuantity(), orderDetail.getDiscount());
            return (Boolean) execute;
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    public static OrderDetailController getInstance(){
        if(instance==null){
            instance=new OrderDetailController();
        }
        return instance;
    }

}
