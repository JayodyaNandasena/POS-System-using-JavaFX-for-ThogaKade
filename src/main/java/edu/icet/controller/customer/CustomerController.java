package edu.icet.controller.customer;

import edu.icet.crudUtil.CrudUtil;
import edu.icet.db.DBConnection;
import edu.icet.model.Customer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerController implements CustomerService{
    private static CustomerController instance;
    private CustomerController(){

    }
    public static CustomerController getInstance(){
        if (instance==null){
            instance=new CustomerController();
        }
        return  instance;
    }
    @Override
    public ObservableList<Customer> getAllCustomers(){
        try {
            ResultSet resultSet= CrudUtil.execute("SELECT * FROM customer");

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
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
        return null;
    }

    @Override
    public boolean addCustomer(Customer customer){
        String sql="INSERT INTO customer VALUES (?,?,?,?,?,?,?,?,?)";

        try {
            return CrudUtil.execute(
                    sql,
                    customer.getId(),
                    customer.getTitle(),
                    customer.getName(),
                    customer.getDob(),
                    customer.getSalary(),
                    customer.getAddress(),
                    customer.getCity(),
                    customer.getProvince(),
                    customer.getPostalCode()
            );
        } catch (SQLException | ClassNotFoundException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
        return false;
    }

    @Override
    public Customer searchCustomer(String customerID){
        String sql = "SELECT * FROM customer WHERE CustID=?";
        try {
            ResultSet resultSet = CrudUtil.execute(sql, customerID);

            while (resultSet.next()) {
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
            alert.showAndWait();
        }
        return null;
    }
}
