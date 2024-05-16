package edu.icet.controller.item;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import edu.icet.controller.item.ItemController;
import edu.icet.db.DBConnection;
import edu.icet.db.StageManager;
import edu.icet.model.Item;
import edu.icet.model.tm.ItemTableTM;
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
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ManageItemFormController implements Initializable {
    public JFXTextField txtItemCode;
    public JFXTextArea txtDescription;
    public JFXTextField txtPackSize;
    public ComboBox cmbSizeUnit;
    public JFXTextField txtPrice;
    public JFXTextField txtQtyOnHand;
    public TableView tblItemData;
    public TableColumn colItemCode;
    public TableColumn colDescription;
    public TableColumn colPackSize;
    public TableColumn colUnitPrice;
    public TableColumn colQtyOnHand;
    public JFXButton btnNavCustomers;
    public JFXButton btnNavItems;
    public JFXButton btnNavOrders;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //binding table columns with model class properties
        colItemCode.setCellValueFactory(new PropertyValueFactory<>("itemCode"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colPackSize.setCellValueFactory(new PropertyValueFactory<>("packSize"));
        colUnitPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colQtyOnHand.setCellValueFactory(new PropertyValueFactory<>("qtyOnHand"));

        loadButtons();

        loadDropMenu();
        loadTable();
    }

    public void btnDeleteOnAction(ActionEvent actionEvent) {
        if (!ItemController.getInstance().deleteItem(txtItemCode.getText())){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Invalid Item Code!");
            alert.show();
            return;
        }

        loadTable();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText("Item Deleted Successfully!");
        alert.show();

        clearText();
    }

    public void btnEditOnAction(ActionEvent actionEvent) {
        String packSize = txtPackSize.getText() + cmbSizeUnit.getValue();
        Item item=new Item(
                txtItemCode.getText(),
                txtDescription.getText(),
                packSize,
                Double.parseDouble(txtPrice.getText()),
                Integer.parseInt(txtQtyOnHand.getText()));

        if(!ItemController.getInstance().updateItem(item)){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("An Error Occurred!\nPlease Check Your Input");
            alert.show();
            return;
        }

        loadTable();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setContentText("Item Updated Successfully!");
        alert.show();

        clearText();

    }

    public void btnAddOnAction(ActionEvent actionEvent) {
        String packSize = txtPackSize.getText() + cmbSizeUnit.getValue();
        Item item = new Item(
                txtItemCode.getText(),
                txtDescription.getText(),
                packSize,
                Double.parseDouble(txtPrice.getText()),
                Integer.parseInt(txtQtyOnHand.getText())
        );

        if (!ItemController.getInstance().addItem(item)){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("An Error Occurred!\nPlease Check Your Input");
            alert.show();

            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText("Item added successfully!");
        alert.show();

        loadTable();
        clearText();
    }

    public void btnSearchOnAction(ActionEvent actionEvent) {
        Item item = ItemController.getInstance().searchItem(txtItemCode.getText());

        if (item==null){
            txtItemCode.setFocusColor(Color.rgb(255, 234, 167));
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Incorrect Item Code");
            alert.showAndWait();
            return;
        }

        String[] packSize = item.getPackSize().split("(?<=\\d)(?=\\D)");

        String packSizeValue = packSize[0];
        String packSizeUnit = packSize[1];

        txtDescription.setText(item.getDescription());
        txtPackSize.setText(packSizeValue);
        cmbSizeUnit.setValue(packSizeUnit);
        txtPrice.setText(item.getUnitPrice().toString());
        txtQtyOnHand.setText(item.getQtyOnHand().toString());
    }


    public void loadDropMenu() {
        ObservableList<String> units = FXCollections.observableArrayList();
        units.add("kg");
        units.add("g");
        units.add("L");
        units.add("ml");
        cmbSizeUnit.setItems(units);
    }

    public void clearText() {
        txtItemCode.clear();
        txtDescription.clear();
        txtPrice.clear();
        txtPackSize.clear();
        txtQtyOnHand.clear();
        cmbSizeUnit.setValue(null);
    }

    public void loadTable() {
        ObservableList<ItemTableTM> itemTableData = FXCollections.observableArrayList();

        ItemController.getInstance().getAllItems().forEach(item -> {
            ItemTableTM itemTable = new ItemTableTM(
                    item.getItemCode(),
                    item.getDescription(),
                    item.getPackSize(),
                    item.getUnitPrice(),
                    item.getQtyOnHand()
            );
            itemTableData.add(itemTable);
        });
        tblItemData.setItems(itemTableData);
    }

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

    public void btnNavCustomersOnAction(ActionEvent actionEvent) {
        Stage currentStage= StageManager.getCurrentStage();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/manage-customer-form.fxml"));
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
        }
    }

    public void btnNavItemsOnAction(ActionEvent actionEvent) {
    }
}
