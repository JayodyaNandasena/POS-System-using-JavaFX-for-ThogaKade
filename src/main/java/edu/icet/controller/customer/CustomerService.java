package edu.icet.controller.customer;

import edu.icet.model.Customer;
import javafx.collections.ObservableList;

public interface CustomerService {
    boolean addCustomer(Customer customer);
    Customer searchCustomer(String customerID);
    ObservableList<Customer> getAllCustomers();
}
