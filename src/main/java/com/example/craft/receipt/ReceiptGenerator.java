package com.example.craft.receipt;

import com.example.craft.domain.Order;

public class ReceiptGenerator {

    public String generate(Order order, int subtotal, int discount, int deliveryFee, int total) {
        return "Receipt\n"
                + "-------\n"
                + "Order: " + order.getOrderId() + "\n"
                + "Customer: " + order.getCustomer().getName() + "\n"
                + "Subtotal: £" + formatPounds(subtotal) + "\n"
                + "Discount: £" + formatPounds(discount) + "\n"
                + "Delivery: £" + formatPounds(deliveryFee) + "\n"
                + "Total: £" + formatPounds(total) + "\n";
    }

    private String formatPounds(int pence) {
        return String.format("%.2f", pence / 100.0);
    }
}