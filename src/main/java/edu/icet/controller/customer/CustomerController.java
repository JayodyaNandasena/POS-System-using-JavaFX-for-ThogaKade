package edu.icet.controller.customer;

import edu.icet.crudUtil.CrudUtil;
import edu.icet.db.DBConnection;
import edu.icet.model.Customer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerController {
    private static CustomerController instance;
    private CustomerController(){

    }
    public static CustomerController getInstance(){
        if (instance==null){
            instance=new CustomerController();
        }
        return  instance;
    }
    public ObservableList<Customer> getAllCustomers(){
        try {
            Customer allCustomers = CrudUtil.execute("SELECT * FROM customer");
            ResultSet resultSet= DBConnection.getInstance().getConnection().createStatement().executeQuery("SELECT * FROM customer");

            ObservableList<Customer> customerObservableList= FXCollections.observableArrayList();

            while (resultSet.next()){
                //mapping the resultSet to a Customer object
                customerObservableList.add(
                        new Customer(
                            resultSet.getString(1),
                            resultSet.getString(2),
                            resultSet.getString(3),
                            resultSet.getDate(4).toLocalDate(),
                            resultSet.getDouble(5),
                            resultSet.getString(6),
                            resultSet.getString(7),
                            resultSet.getString(8),
                            resultSet.getString(9)
                    )
                );
            }
            return customerObservableList;
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public Customer searchCustomer(String customerID){
        try {
            Connection connection=DBConnection.getInstance().getConnection();
            ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM customer WHERE CustID='" + customerID + "'");
            while (resultSet.next()){
                return new Customer(
                        resultSet.getString(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getDate(4).toLocalDate(),
                        resultSet.getDouble(5),
                        resultSet.getString(6),
                        resultSet.getString(7),
                        resultSet.getString(8),
                        resultSet.getString(9)
                );
            }

        } catch (ClassNotFoundException | SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(e.getMessage());
            alert.show();
        }
        return null;
    }
}
