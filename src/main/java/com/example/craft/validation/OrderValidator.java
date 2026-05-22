package com.example.craft.validation;

import com.example.craft.domain.Customer;
import com.example.craft.domain.Order;
import com.example.craft.domain.OrderItem;

public class OrderValidator {

    public void validate(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("Order must not be null");
        }

        if (order.getCustomer() == null) {
            throw new IllegalArgumentException("Customer must not be null");
        }

        if (order.getItems().isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item");
        }

        Customer customer = order.getCustomer();

        if (customer.getName() == null || customer.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name is invalid");
        }

        if (customer.getEmail() == null || !customer.getEmail().contains("@")) {
            throw new IllegalArgumentException("Customer email is invalid");
        }

        if (customer.getType() == null) {
            throw new IllegalArgumentException("Customer type is required");
        }

        for (OrderItem item : order.getItems()) {
            if (item == null) {
                throw new IllegalArgumentException("Order item must not be null");
            }

            if (item.getQuantity() <= 0) {
                throw new IllegalArgumentException("Item quantity must be positive");
            }

            if (item.getUnitPricePence() <= 0) {
                throw new IllegalArgumentException("Item price must be positive");
            }
        }
    }
}