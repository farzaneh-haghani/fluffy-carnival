package com.example.craft;

import com.example.craft.domain.Customer;
import com.example.craft.domain.CustomerType;
import com.example.craft.domain.Order;
import com.example.craft.domain.OrderItem;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderProcessorTest {

        private final OrderProcessor processor = new OrderProcessor();

        @Test
        void student_customer_gets_discount_and_receipt_contains_total() {
                Customer customer = new Customer("Ada", "ada@example.com", "07123456789", CustomerType.STUDENT);
                Order order = new Order("ORD-1", customer, "STANDARD", "CARD");
                order.addItem(new OrderItem("Book", 1, 1000));

                String receipt = processor.process(order);

                assertTrue(receipt.contains("Order: ORD-1"));
                assertTrue(receipt.contains("Discount: £1.50"));
                assertTrue(receipt.contains("Delivery: £3.99"));
                assertTrue(receipt.contains("Total: £12.49"));
        }

        @Test
        void staffDiscountAppliedCorrectly() {
                Customer customer = new Customer(
                                "Sam",
                                "sam@test.com",
                                "07123456789",
                                CustomerType.STAFF);

                Order order = new Order("ORD-002", customer, "STANDARD", "CARD");
                order.addItem(new OrderItem("Laptop", 1, 25000));

                String receipt = processor.process(order);

                assertTrue(receipt.contains("Subtotal: £250.00"));
                assertTrue(receipt.contains("Discount: £55.00"));
                assertTrue(receipt.contains("Delivery: £0.00"));
                assertTrue(receipt.contains("Total: £195.00"));
        }

        @Test
        void standardDeliveryBecomesFreeOverCorrectAmount() {
                Customer customer = new Customer(
                                "Ben",
                                "ben@test.com",
                                "07123456789",
                                CustomerType.STANDARD);

                Order order = new Order("ORD-003", customer, "STANDARD", "CARD");
                order.addItem(new OrderItem("Shoes", 1, 5001));

                String receipt = processor.process(order);

                assertTrue(receipt.contains("Subtotal: £50.01"));
                assertTrue(receipt.contains("Discount: £0.00"));
                assertTrue(receipt.contains("Delivery: £0.00"));
                assertTrue(receipt.contains("Total: £50.01"));
        }

        @Test
        void invalidDeliveryTypeThrowsException() {
                Customer customer = new Customer(
                                "Cara",
                                "cara@test.com",
                                "07123456789",
                                CustomerType.STANDARD);

                Order order = new Order("ORD-004", customer, "DRONE", "CARD");
                order.addItem(new OrderItem("Keyboard", 1, 3000));

                IllegalArgumentException exception = assertThrows(
                                IllegalArgumentException.class,
                                () -> processor.process(order));

                assertTrue(exception.getMessage().contains("Unknown delivery type"));
        }

        @Test
        void emptyOrderShouldBeRejected() {
                Customer customer = new Customer(
                                "Dan",
                                "dan@test.com",
                                "07123456789",
                                CustomerType.STANDARD);

                Order order = new Order("ORD-005", customer, "STANDARD", "CARD");

                IllegalArgumentException exception = assertThrows(
                                IllegalArgumentException.class,
                                () -> processor.process(order));

                assertEquals("Order must contain at least one item", exception.getMessage());
        }

        @Test
        void collectionHasNoDeliveryFeeAdded() {
                Customer customer = new Customer(
                                "Ella",
                                "ella@test.com",
                                "07123456789",
                                CustomerType.STANDARD);

                Order order = new Order("ORD-006", customer, "COLLECTION", "CARD");
                order.addItem(new OrderItem("Notebook", 1, 3000));

                String receipt = processor.process(order);

                assertTrue(receipt.contains("Subtotal: £30.00"));
                assertTrue(receipt.contains("Discount: £0.00"));
                assertTrue(receipt.contains("Delivery: £0.00"));
                assertTrue(receipt.contains("Total: £30.00"));
        }

        @Test
        void premiumCustomerGetsLargerDiscountWhenMoreThanFiveItems() {
                Customer customer = new Customer(
                                "Priya",
                                "priya@test.com",
                                "07123456789",
                                CustomerType.PREMIUM);

                Order order = new Order("ORD-007", customer, "STANDARD", "CARD");
                order.addItem(new OrderItem("Pen", 6, 1000));

                String receipt = processor.process(order);

                assertTrue(receipt.contains("Subtotal: £60.00"));
                assertTrue(receipt.contains("Discount: £9.00"));
                assertTrue(receipt.contains("Delivery: £0.00"));
                assertTrue(receipt.contains("Total: £51.00"));
        }

        @Test
        void invalidPaymentTypeThrowsException() {
                Customer customer = new Customer(
                                "Pat",
                                "pat@test.com",
                                "07123456789",
                                CustomerType.STANDARD);

                Order order = new Order("ORD-009", customer, "STANDARD", "CRYPTO");
                order.addItem(new OrderItem("Mouse", 1, 2500));

                IllegalArgumentException exception = assertThrows(
                                IllegalArgumentException.class,
                                () -> processor.process(order));

                assertTrue(exception.getMessage().contains("Unknown payment type"));
        }

        @Test
        void loyaltyCustomerGetsExpectedDiscount() {
                Customer customer = new Customer(
                                "Lara",
                                "lara@test.com",
                                "07123456789",
                                CustomerType.LOYALTY);

                Order order = new Order("ORD-008", customer, "STANDARD", "CARD");
                order.addItem(new OrderItem("Notebook", 4, 2500));

                String receipt = processor.process(order);

                assertTrue(receipt.contains("Subtotal: £100.00"));
                assertTrue(receipt.contains("Discount: £15.50"));
                assertTrue(receipt.contains("Delivery: £0.00"));
                assertTrue(receipt.contains("Total: £84.50"));
        }
}
