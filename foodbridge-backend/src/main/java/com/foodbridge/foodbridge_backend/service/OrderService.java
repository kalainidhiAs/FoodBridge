package com.foodbridge.foodbridge_backend.service;

import com.foodbridge.foodbridge_backend.model.*;
import com.foodbridge.foodbridge_backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class OrderService {

    @Autowired private OrderRepository orderRepository;
    @Autowired private OrderItemRepository orderItemRepository;
    @Autowired private FoodListingRepository foodListingRepository;
    @Autowired private EarningRepository earningRepository;
    @Autowired private NotificationService notificationService;

    public Order placeOrder(User customer, List<Map<String, Object>> cartItems, String deliveryAddress) {
        if (cartItems == null || cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty.");
        }

        Order order = new Order();
        order.setCustomer(customer);
        order.setStatus("PENDING");
        order.setPaymentMethod("COD");
        order.setPaymentStatus("PENDING_PAYMENT");
        order.setDeliveryAddress(deliveryAddress);
        order.setOrderTime(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        double total = 0.0;
        User homemaker = null;

        for (Map<String, Object> item : cartItems) {
            Long listingId = Long.valueOf(item.get("listingId").toString());
            int qty = Integer.parseInt(item.get("quantity").toString());
            FoodListing listing = foodListingRepository.findById(listingId)
                    .orElseThrow(() -> new RuntimeException("Food item not found: " + listingId));
            if (homemaker == null) homemaker = listing.getHomemaker();
            if (listing.getQuantity() < qty) {
                throw new RuntimeException("Insufficient stock for: " + listing.getFoodName());
            }
            listing.setQuantity(listing.getQuantity() - qty);
            foodListingRepository.save(listing);
            total += listing.getPrice() * qty;
        }

        order.setHomemaker(homemaker);
        order.setTotalAmount(Math.round(total * 100.0) / 100.0);
        Order savedOrder = orderRepository.save(order);

        for (Map<String, Object> item : cartItems) {
            Long listingId = Long.valueOf(item.get("listingId").toString());
            int qty = Integer.parseInt(item.get("quantity").toString());
            FoodListing listing = foodListingRepository.findById(listingId).get();
            OrderItem oi = new OrderItem();
            oi.setOrder(savedOrder);
            oi.setFoodListing(listing);
            oi.setQuantity(qty);
            oi.setUnitPrice(listing.getPrice());
            oi.setSubtotal(listing.getPrice() * qty);
            orderItemRepository.save(oi);
        }

        if (homemaker != null) {
            notificationService.send(homemaker,
                    "New order #" + savedOrder.getId() + " received from " + customer.getName() + ". Total: ₹" + savedOrder.getTotalAmount(),
                    "ORDER");
        }

        return savedOrder;
    }

    public Order acceptOrder(Long orderId, User homemaker) {
        Order order = getOrderForHomemaker(orderId, homemaker);
        if (!"PENDING".equals(order.getStatus())) throw new RuntimeException("Cannot accept this order.");
        order.setStatus("CONFIRMED");
        order.setUpdatedAt(LocalDateTime.now());
        Order saved = orderRepository.save(order);
        notificationService.send(order.getCustomer(),
                "Your order #" + orderId + " has been confirmed by the homemaker!", "ORDER");
        return saved;
    }

    public Order rejectOrder(Long orderId, User homemaker, String remark) {
        Order order = getOrderForHomemaker(orderId, homemaker);
        if (!"PENDING".equals(order.getStatus())) throw new RuntimeException("Cannot reject this order.");
        order.setStatus("REJECTED");
        order.setRejectionRemark(remark);
        order.setUpdatedAt(LocalDateTime.now());
        // Restore stock
        List<OrderItem> items = orderItemRepository.findByOrder(order);
        items.forEach(oi -> {
            FoodListing fl = oi.getFoodListing();
            fl.setQuantity(fl.getQuantity() + oi.getQuantity());
            foodListingRepository.save(fl);
        });
        Order saved = orderRepository.save(order);
        notificationService.send(order.getCustomer(),
                "Your order #" + orderId + " was rejected. Reason: " + remark, "ORDER");
        return saved;
    }

    public Order updateStatus(Long orderId, User homemaker, String newStatus) {
        Order order = getOrderForHomemaker(orderId, homemaker);
        List<String> validFlow = List.of("CONFIRMED", "OUT_FOR_DELIVERY", "COMPLETED");
        int current = validFlow.indexOf(order.getStatus());
        int next = validFlow.indexOf(newStatus);
        if (next != current + 1) throw new RuntimeException("Invalid status transition.");
        order.setStatus(newStatus);
        order.setUpdatedAt(LocalDateTime.now());

        if ("COMPLETED".equals(newStatus)) {
            order.setPaymentStatus("PAID");
            recordEarning(order);
            notificationService.send(order.getCustomer(),
                    "Your order #" + orderId + " has been delivered! Please leave a review.", "ORDER");
        } else {
            notificationService.send(order.getCustomer(),
                    "Your order #" + orderId + " is now: " + newStatus, "ORDER");
        }
        return orderRepository.save(order);
    }

    public Order payOrder(Long orderId, User customer) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        if (!order.getCustomer().getId().equals(customer.getId())) {
            throw new RuntimeException("Not authorized.");
        }
        if (!"CONFIRMED".equals(order.getStatus()) && !"OUT_FOR_DELIVERY".equals(order.getStatus())) {
            throw new RuntimeException("Order must be CONFIRMED or OUT_FOR_DELIVERY to be paid.");
        }
        order.setStatus("COMPLETED");
        order.setPaymentStatus("PAID");
        order.setUpdatedAt(LocalDateTime.now());
        recordEarning(order);
        
        notificationService.send(order.getHomemaker(),
                "Customer paid for order #" + orderId + ". Please deliver the food.", "ORDER");
                
        return orderRepository.save(order);
    }

    public List<Order> getCustomerOrders(User customer) {
        return orderRepository.findByCustomerOrderByOrderTimeDesc(customer);
    }

    public List<Order> getHomemakerOrders(User homemaker) {
        return orderRepository.findByHomemakerOrderByOrderTimeDesc(homemaker);
    }

    private Order getOrderForHomemaker(Long orderId, User homemaker) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        if (!order.getHomemaker().getId().equals(homemaker.getId())) {
            throw new RuntimeException("Not authorized to manage this order.");
        }
        return order;
    }

    private void recordEarning(Order order) {
        LocalDateTime now = LocalDateTime.now();
        Earning earning = new Earning();
        earning.setHomemaker(order.getHomemaker());
        earning.setOrder(order);
        earning.setAmount(order.getTotalAmount());
        earning.setMonth(now.getMonthValue());
        earning.setYear(now.getYear());
        earning.setFeeStatus("PENDING");
        earning.setEarningTime(now);
        earningRepository.save(earning);
    }
}
