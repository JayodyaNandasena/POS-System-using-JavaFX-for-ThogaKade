package edu.icet.controller.order;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import edu.icet.controller.customer.CustomerController;
import edu.icet.controller.item.ItemController;
import edu.icet.db.DBConnection;
import edu.icet.db.StageManager;
import edu.icet.model.Customer;
import edu.icet.model.Item;
import edu.icet.model.Order;
import edu.icet.model.OrderDetail;
import edu.icet.model.tm.CartTable;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ManageOrderFormController implements Initializable {
    public JFXButton btnNavCustomers;
    public JFXButton btnNavItems;
    public JFXButton btnNavOrders;
    public JFXTextField lblCustomerName;
    public JFXTextField lblCustomerAddress;
    public DatePicker dtOrderDate;
    public Label lblCurrentTime;
    public ComboBox cmbItemCodes;
    public ComboBox cmbCustomerIDs;
    public Label lblDescription;
    public Label lblQtyOnHand;
    public Label lblUnitPrice;
    public JFXButton btnAddToCart;
    public Label lblOrderId1;
    public TableColumn colItemCode;
    public TableColumn colItemName;
    public TableColumn colQuantity;
    public TableColumn colDiscount;
    public TableColumn colUnitPrice;
    public TableColumn colPrice;
    public JFXTextField txtQuantity;
    public TableView tblCart;
    public Label lblNetTotal;
    public JFXButton btnPlaceOrder;

    ObservableList<CartTable> cartList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colItemCode.setCellValueFactory(new PropertyValueFactory<>("itemCode"));
        colItemName.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("orderQuantity"));
        colDiscount.setCellValueFactory(new PropertyValueFactory<>("discount"));
        colUnitPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));

        loadButtons();
        loadItemCodes();
        loadCustomerIDs();
        loadDateAndTime();
        generateOrderId();

        cmbCustomerIDs.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                    setCustomerDetailsForLabels((String) newValue);
                }
        );

        cmbItemCodes.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                    setItemDetailsForLabels((String) newValue);
                    cmbCustomerIDs.setDisable(true);
                    btnAddToCart.setDisable(false);
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

    public  void generateOrderId() {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM orders");
            Integer count = 0;
            while (resultSet.next()){
                count = resultSet.getInt(1);

            }
            if (count == 0) {
                lblOrderId1.setText("D001");
            }
            String lastOrderId="";
            ResultSet resultSet1 = connection.createStatement().executeQuery("SELECT OrderID\n" +
                    "FROM orders\n" +
                    "ORDER BY OrderID DESC\n" +
                    "LIMIT 1;");
            if (resultSet1.next()){
                lastOrderId = resultSet1.getString(1);
                Pattern pattern = Pattern.compile("[A-Za-z](\\d+)");
                Matcher matcher = pattern.matcher(lastOrderId);
                if (matcher.find()) {
                    int number = Integer.parseInt(matcher.group(1));
                    number++;
                    lblOrderId1.setText(String.format("D%03d", number));
                } else {
                    new Alert(Alert.AlertType.WARNING,"hello").show();
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public void btnAddToCartOnAction(ActionEvent actionEvent) {
        String itemCode =(String)cmbItemCodes.getValue();
        String desc = lblDescription.getText();
        Integer qtyForCustomer =Integer.parseInt(txtQuantity.getText());
        Double unitPrice = Double.valueOf(lblUnitPrice.getText());
        Double total =  qtyForCustomer*unitPrice;
        CartTable cartTbl = new CartTable(itemCode, desc, qtyForCustomer, 0.0, unitPrice, total);

        int qtyStock = Integer.parseInt(lblQtyOnHand.getText());
        if(qtyStock<qtyForCustomer){
            new Alert(Alert.AlertType.WARNING,"Invalid QTY").show();
            return;
        }

        cartList.add(cartTbl);

        tblCart.setItems(cartList);
        calcNetTotal();
        btnPlaceOrder.setDisable(false);
    }
    public void calcNetTotal(){
        double ttl=0;
        for (CartTable cartObj : cartList){
            ttl+=cartObj.getPrice();
        }
        lblNetTotal.setText(ttl+"/=");
    }
    public void btnPlaceOrderOnAction(ActionEvent actionEvent) {
        try {
            String orderID = lblOrderId1.getText();
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date orderDate = format.parse(String.valueOf(dtOrderDate.getValue()));
            String customerID = cmbCustomerIDs.getValue().toString();

            List<OrderDetail> orderDetailList = new ArrayList<>();

            for (CartTable cartTable : cartList){
                String itemCode = cartTable.getItemCode();
                Integer orderQuantity = cartTable.getOrderQuantity();
                Double discount = cartTable.getDiscount();

                orderDetailList.add(new OrderDetail(orderID, itemCode, orderQuantity, discount));
            }

            Order order = new Order(orderID,orderDate,customerID,orderDetailList);

            Boolean isOrderPlaced = OrderController.getInstance().placeOrder(order);

            if (isOrderPlaced) {
                new Alert(Alert.AlertType.CONFIRMATION, "Order Placed Successfully!").show();
                return;
            }
            new Alert(Alert.AlertType.ERROR, "Error Placing the Order!!!").show();


        } catch (ParseException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }
    public void btnCommitOnAction(ActionEvent actionEvent) {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            connection.setAutoCommit(true);
            System.out.println("Commit "+connection.getAutoCommit());



        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void btnRollBackOnAction(ActionEvent actionEvent) {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            connection.rollback();
            System.out.println("RollBack : Commit "+connection.getAutoCommit());



        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void btnAddOnAction(ActionEvent actionEvent) {
    }

    public void btnEditOnAction(ActionEvent actionEvent) {
    }

    public void btnDeleteOnAction(ActionEvent actionEvent) {
    }

    //Buttons for navigation
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


    public void btnClearAllOnAction(ActionEvent actionEvent) {
        loadDateAndTime();
        generateOrderId();

        cmbCustomerIDs.setDisable(false);
        btnPlaceOrder.setDisable(true);
        btnAddToCart.setDisable(true);

        lblNetTotal.setText("0.00");
        lblDescription.setText("");
        lblQtyOnHand.setText("");
        lblUnitPrice.setText("");
        txtQuantity.setText("");
        lblCustomerName.setText("");
        lblCustomerAddress.setText("");

        cmbCustomerIDs.setValue(null);
        //cmbItemCodes.setValue(null);

        cartList.clear();
        tblCart.setItems(null);

    }
}
