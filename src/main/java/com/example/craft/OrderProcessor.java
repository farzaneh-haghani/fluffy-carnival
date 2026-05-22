package com.example.craft;

import com.example.craft.discount.DiscountStrategy;
import com.example.craft.discount.DiscountStrategyFactory;
import com.example.craft.domain.Customer;
import com.example.craft.domain.CustomerType;
import com.example.craft.domain.Order;
import com.example.craft.domain.OrderItem;

import com.example.craft.notification.ConsoleNotifier;
import com.example.craft.notification.LegacySmsClient;
import com.example.craft.notification.Notifier;
import com.example.craft.notification.SmsNotifierAdapter;

import com.example.craft.delivery.DeliveryStrategy;
import com.example.craft.delivery.DeliveryStrategyFactory;

import com.example.craft.payment.PaymentProcessor;
import com.example.craft.payment.PaymentProcessorFactory;

import com.example.craft.validation.OrderValidator;

import com.example.craft.receipt.ReceiptGenerator;


public class OrderProcessor {

    private final DiscountStrategyFactory discountStrategyFactory = new DiscountStrategyFactory();
    private final Notifier emailNotifier = new ConsoleNotifier();
    private final Notifier smsNotifier = new SmsNotifierAdapter(new LegacySmsClient());
    private final DeliveryStrategyFactory deliveryStrategyFactory = new DeliveryStrategyFactory();
    private final PaymentProcessorFactory paymentProcessorFactory = new PaymentProcessorFactory();
    private final OrderValidator orderValidator = new OrderValidator();
    private final ReceiptGenerator receiptGenerator = new ReceiptGenerator();


    public String process(Order order) {
        orderValidator.validate(order);

        int subtotal = calculateSubtotal(order);
        int discount = calculateDiscount(order.getCustomer(), order, subtotal);
        int deliveryFee = calculateDeliveryFee(order, subtotal);

        int total = subtotal - discount + deliveryFee;

        if (total <= 0) {
            throw new IllegalStateException("Order total must be positive");
        }

        processPayment(order, total);
        sendNotifications(order, total);

        String receipt = receiptGenerator.generate(order, subtotal, discount, deliveryFee, total);
        System.out.println(receipt);
        return receipt;
    }

    private int calculateSubtotal(Order order) {
        int subtotal = 0;

        for (OrderItem item : order.getItems()) {

            subtotal = subtotal + item.getQuantity() * item.getUnitPricePence();

        }
        return subtotal;
    }

    private int calculateDiscount(Customer customer, Order order, int subtotal) {
        DiscountStrategy strategy = discountStrategyFactory.getStrategy(customer.getType());
        return strategy.calculateDiscount(order, subtotal);
    }

    private int calculateDeliveryFee(Order order, int subtotal) {
        DeliveryStrategy strategy = deliveryStrategyFactory.getStrategy(order.getDeliveryType());
        return strategy.calculateDeliveryFee(order, subtotal);
    }

    private void processPayment(Order order, int total) {
        PaymentProcessor processor = paymentProcessorFactory.getProcessor(order.getPaymentType());
        processor.processPayment(order, total);
    }

    private void sendNotifications(Order order, int total) {
        Customer customer = order.getCustomer();

        System.out.println("Saving order " + order.getOrderId());
        System.out.println("Saving order " + order.getOrderId() + " for customer " + customer.getName());

        String message = "Dear " + customer.getName()
                + ", your order " + order.getOrderId()
                + " has been processed. Total: £" + formatPounds(total);

        emailNotifier.notifyCustomer(customer, message);

        if (customer.getPhoneNumber() != null && customer.getPhoneNumber().startsWith("07")) {
            smsNotifier.notifyCustomer(customer, "Order " + order.getOrderId() + " confirmed by SMS");
        }

        if (customer.getType() == CustomerType.PREMIUM && total > 5000) {
            emailNotifier.notifyCustomer(customer, "Thank you for being a premium customer.");
        }
    }

    private String formatPounds(int pence) {
        return String.format("%.2f", pence / 100.0);
    }
}
