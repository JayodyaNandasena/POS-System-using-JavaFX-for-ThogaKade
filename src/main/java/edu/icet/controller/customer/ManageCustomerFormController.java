package edu.icet.controller.customer;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import edu.icet.db.DBConnection;
import edu.icet.db.StageManager;
import edu.icet.model.Customer;
import edu.icet.model.tm.Table01TM;
import edu.icet.model.tm.Table02TM;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.*;



public class ManageCustomerFormController implements Initializable {
    public JFXTextField txtCustomerID;
    public JFXTextField txtName;
    public JFXTextField txtSalary;
    public JFXTextField txtAddress;
    public JFXTextField txtCity;
    public JFXTextField txtProvince;
    public JFXTextField txtPostalCode;
    public JFXComboBox cmbTitle;
    public DatePicker dateDOB;
    public TableView tblCustomer01, tblCustomer02;
    public TableColumn colCustomerID01, colTitle, colName, colDob, colSalary, colCustomerId02,
            colAddress, colCity, colProvince, colPostalCode;
    public JFXButton btnNavCustomers;
    public JFXButton btnNavItems;
    public JFXButton btnNavOrders;


    @Override
    // Initializes JavaFX elements or sets initial values after the FXML file is loaded
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colCustomerID01.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colDob.setCellValueFactory(new PropertyValueFactory<>("dob"));
        colSalary.setCellValueFactory(new PropertyValueFactory<>("salary"));
        colCustomerId02.setCellValueFactory(new PropertyValueFactory<>("id"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colCity.setCellValueFactory(new PropertyValueFactory<>("city"));
        colProvince.setCellValueFactory(new PropertyValueFactory<>("province"));
        colPostalCode.setCellValueFactory(new PropertyValueFactory<>("postalCode"));

        loadButtons();
        loadDropMenu();
        loadTable01();
        loadTable02();
    }

    public void btnSearchOnAction(ActionEvent actionEvent) {
        Customer customer = CustomerController.getInstance().searchCustomer(txtCustomerID.getText());

        if (customer != null) {
            txtCustomerID.setText(customer.getId());
            cmbTitle.setValue(customer.getTitle());
            txtName.setText(customer.getName());
            dateDOB.setValue(customer.getDob());
            txtSalary.setText(customer.getSalary().toString());
            txtAddress.setText(customer.getAddress());
            txtCity.setText(customer.getCity());
            txtProvince.setText(customer.getProvince());
            txtPostalCode.setText(customer.getPostalCode());
        }else{
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setContentText("No Customer Found!");
            alert.show();
        }
    }

    public void btnAddOnAction(ActionEvent actionEvent) throws ParseException {
        Customer customer = new Customer(txtCustomerID.getText(),
                cmbTitle.getValue().toString(),
                txtName.getText(),
                dateDOB.getValue(),
                Double.parseDouble(txtSalary.getText()),
                txtAddress.getText(),
                txtCity.getText(),
                txtProvince.getText(),
                txtPostalCode.getText()
        );

        if (CustomerController.getInstance().addCustomer(customer)){
            loadTable01();
            loadTable02();

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setContentText("Customer Added Successfully!");
            alert.show();

            cleartext();
        }

    }

    public void btnEditOnAction(ActionEvent actionEvent) {
    }

    public void btnDeleteOnAction(ActionEvent actionEvent) {
        try {
            Connection connection=DBConnection.getInstance().getConnection();
            boolean deleteStatus = connection.createStatement().execute("DELETE FROM customer WHERE CustID='" + txtCustomerID.getText() + "'");

            if (!deleteStatus) {
                loadTable01();
                loadTable02();

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setContentText("Customer Deleted Successfully!");
                alert.show();

                cleartext();
            }

        } catch (ClassNotFoundException | SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(e.getMessage());
            alert.show();
        }
    }

    private void loadDropMenu() {
        ObservableList<String> titles = FXCollections.observableArrayList();
        titles.add("Mr.");
        titles.add("Mrs.");
        titles.add("Miss.");
        cmbTitle.setItems(titles);
    }

    private void loadTable01(){
        ObservableList<Table01TM> table01Data = FXCollections.observableArrayList();

        CustomerController.getInstance().getAllCustomers().forEach(customer -> {
            Table01TM table01 = new Table01TM(
                    customer.getId(),
                    customer.getTitle(),
                    customer.getName(),
                    customer.getDob(),
                    customer.getSalary()
            );
            table01Data.add(table01);
        });

        tblCustomer01.setItems(table01Data);
    }

    private void loadTable02(){
        ObservableList<Table02TM> table02Data = FXCollections.observableArrayList();
        CustomerController.getInstance().getAllCustomers().forEach(customer -> {
            Table02TM table02 = new Table02TM(
                    customer.getId(),
                    customer.getAddress(),
                    customer.getCity(),
                    customer.getProvince(),
                    customer.getPostalCode()
            );
            table02Data.add(table02);
        });
        tblCustomer02.setItems(table02Data);
    }

    private void cleartext(){
        txtCustomerID.clear();
        cmbTitle.setValue(null);
        dateDOB.setValue(null);
        txtName.clear();
        txtSalary.clear();
        txtAddress.clear();
        txtCity.clear();
        txtProvince.clear();
        txtPostalCode.clear();
    }

//    menuItem.setOnAction(new EventHandler<ActionEvent>() {
//        @Override
//        public void handle(ActionEvent event) {
//            System.out.println("Menu item Clicked");
//        }
//    });

    public void loadButtons(){
        final double BUTTON_DIMENSION = 25;

        //Create imageview with background image
        ImageView view1 = new ImageView(new Image("/icons/manage-customer-icon.png"));
        view1.setFitHeight(BUTTON_DIMENSION);
        view1.setFitWidth(BUTTON_DIMENSION);
        view1.setPreserveRatio(true);

        //Attach image to the button
        btnNavCustomers.setGraphic(view1);
        //Set the image to the top
        btnNavCustomers.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

        //Create imageview with background image
        ImageView view2 = new ImageView(new Image("/icons/manage-items-icon.png"));
        view2.setFitHeight(BUTTON_DIMENSION);
        view2.setFitWidth(BUTTON_DIMENSION);
        view2.setPreserveRatio(true);

        //Attach image to the button
        btnNavItems.setGraphic(view2);
        //Set the image to the top
        btnNavItems.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

        //Create imageview with background image
        ImageView view3 = new ImageView(new Image("/icons/manage-orders-icon.png"));
        view3.setFitHeight(BUTTON_DIMENSION);
        view3.setFitWidth(BUTTON_DIMENSION);
        view3.setPreserveRatio(true);

        //Attach image to the button
        btnNavOrders.setGraphic(view3);
        //Set the image to the top
        btnNavOrders.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
    }
    public void btnNavOrdersOnAction(ActionEvent actionEvent) {
        Stage currentStage= StageManager.getCurrentStage();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/manage-order-form.fxml"));
        Parent root;
        try {
            root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

            if (currentStage != null) {
                currentStage.close(); // Close the current stage if it exists
            }

            StageManager.setCurrentStage(stage);
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(e.getMessage());
            alert.show();
            System.out.println(e.getMessage());
        }
    }

    public void btnNavItemsOnAction(ActionEvent actionEvent) {
        Stage currentStage= StageManager.getCurrentStage();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/manage-item-form.fxml"));
        Parent root;
        try {
            root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

            if (currentStage != null) {
                currentStage.close(); // Close the current stage if it exists
            }

            StageManager.setCurrentStage(stage);
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(e.getMessage());
            alert.show();
        }
    }

}
