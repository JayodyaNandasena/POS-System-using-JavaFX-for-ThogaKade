package edu.icet.controller.order;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import edu.icet.controller.customer.CustomerController;
import edu.icet.controller.item.ItemController;
import edu.icet.crudUtil.CrudUtil;
import edu.icet.db.DBConnection;
import edu.icet.db.StageManager;
import edu.icet.model.Customer;
import edu.icet.model.Item;
import edu.icet.model.OrderDetail;
import edu.icet.model.tm.OrderTableTM;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ManageOrderFormController implements Initializable {
    public JFXButton btnNavCustomers;
    public JFXButton btnNavItems;
    public JFXButton btnNavOrders;
    public ScrollPane scrlOrders;
    public JFXTextField lblCustomerName;
    public JFXTextField lblCustomerAddress;
    public DatePicker dtOrderDate;
    public Label lblOrderID;
    public Label lblOCustID;
    public Label lblOCustName;
    public Label lblOOrderDate;
    public TableColumn colItemCode;
    public TableColumn colItemName;
    public TableColumn colQuantity;
    public TableColumn colDiscount;
    public TableView tblOrderDetails;
    public Label lblCurrentTime;
    public ComboBox cmbItemCodes;
    public ComboBox cmbCustomerIDs;
    public Label lblDescription;
    public Label lblQtyOnHand;
    public Label lblUnitPrice;

    private List<OrderDetail> orderDetailList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colItemCode.setCellValueFactory(new PropertyValueFactory<>("itemCode"));
        colItemName.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("orderQuantity"));
        colDiscount.setCellValueFactory(new PropertyValueFactory<>("discount"));

        loadButtons();
        loadItemCodes();
        loadCustomerIDs();
        //loadItems();
        //loadOrderDetail();
        //loadTables();
        loadDateAndTime();

        cmbCustomerIDs.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                    setCustomerDetailsForLabels((String) newValue);
                }
        );

        cmbItemCodes.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                    setItemDetailsForLabels((String) newValue);
                }
        );
    }

    private void setItemDetailsForLabels(String itemCode) {
        Item item = ItemController.getInstance().searchItem(itemCode);

        if (item==null){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Incorrect Item Code");
            alert.showAndWait();
        }

        lblDescription.setText(item.getDescription());
        lblQtyOnHand.setText(item.getQtyOnHand().toString());
        lblUnitPrice.setText(item.getUnitPrice().toString());

    }

    private void loadCustomerIDs() {
        ObservableList<Customer> allCustomers = CustomerController.getInstance().getAllCustomers();

        ObservableList customerIds=FXCollections.observableArrayList();

        allCustomers.forEach(customer -> {
            customerIds.add(customer.getId());
        });

        cmbCustomerIDs.setItems(customerIds);
    }

    private void setCustomerDetailsForLabels(String customerID) {
        Customer customer = CustomerController.getInstance().searchCustomer(customerID);

        String name = customer.getTitle() + " " + customer.getName();
        String address = customer.getAddress() + ", " + customer.getCity() + ", " + customer.getProvince() + ", " + customer.getPostalCode();

        lblCustomerName.setText(name);
        lblCustomerAddress.setText(address);
    }

    private void loadItemCodes() {
        ObservableList itemCodes = FXCollections.observableArrayList();
        ObservableList<Item> allItems = ItemController.getInstance().getAllItems();

        allItems.forEach(item -> {
            itemCodes.add(item.getItemCode());
        });
        cmbItemCodes.setItems(itemCodes);
    }

    private void loadDateAndTime() {
        //Disable past dates from the date picker
        dtOrderDate.setValue(LocalDate.now());
        dtOrderDate.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(LocalDate.now()));
            }
        });

        //Load Time
        Timeline timeline = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            LocalTime time = LocalTime.now();
            lblCurrentTime.setText(
                    time.getHour() + " : " + time.getMinute() + " : " + time.getSecond()
            );
        }),
                new KeyFrame(Duration.seconds(1))
        );
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    public void loadItems() {
        try {
            ResultSet resultSet = DBConnection.getInstance().getConnection().createStatement().executeQuery("SELECT * FROM item");
            VBox container = new VBox();

            while (resultSet.next()) {
                Label lblItemCode = new Label(resultSet.getString(1));
                Label lblUnitPrice = new Label(resultSet.getString(4));
                Spinner orderCount = new Spinner(0,
                        Integer.parseInt(resultSet.getString(5)),
                        0);
                Label lblItemAvailableCount = new Label(resultSet.getString(5));

                HBox hboxItem = new HBox();
                hboxItem.setPadding(new Insets(5, 5, 5, 5));

                hboxItem.getChildren().addAll(lblItemCode, lblUnitPrice, orderCount, lblItemAvailableCount);

                container.getChildren().add(hboxItem);

            }
            scrlOrders.setContent(container);

        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadTables() {
        ObservableList<OrderTableTM> orderDetailTableData = FXCollections.observableArrayList();

        orderDetailList.forEach(orderDetail -> {
            OrderTableTM orderTableTM = new OrderTableTM(
                    orderDetail.getItemCode(),
                    orderDetail.getItemName(),
                    orderDetail.getOrderQuantity(),
                    orderDetail.getDiscount()
            );
            orderDetailTableData.add(orderTableTM);
        });
        tblOrderDetails.setItems(orderDetailTableData);
    }

    public void loadOrderDetail() {
        orderDetailList = new ArrayList<>();
        try {
            String sql = "SELECT orderDetail.ItemCode, item.Description, orderDetail.OrderQTY, orderDetail.Discount FROM orderDetail INNER JOIN item ON orderDetail.ItemCode=item.ItemCode";
            ResultSet resultSet = DBConnection.getInstance().getConnection().createStatement().executeQuery(sql);
            while (resultSet.next()) {
                OrderDetail orderDetail = new OrderDetail(
                        resultSet.getString(1),
                        resultSet.getString(2),
                        resultSet.getInt(3),
                        resultSet.getDouble(4)
                );
                orderDetailList.add(orderDetail);
            }
        } catch (SQLException | ClassNotFoundException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(e.getMessage());
            alert.show();
        }
    }

    public void btnAddOnAction(ActionEvent actionEvent) {
    }

    public void btnEditOnAction(ActionEvent actionEvent) {
    }

    public void btnDeleteOnAction(ActionEvent actionEvent) {
    }

    public void loadButtons() {
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
        Stage currentStage = StageManager.getCurrentStage();

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

    public void btnNavItemsOnAction(ActionEvent actionEvent) {
        Stage currentStage = StageManager.getCurrentStage();

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

    public void btnNavOrdersOnAction(ActionEvent actionEvent) {
    }

}
