package uz.kuponbot.kupon.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.kuponbot.kupon.entity.*;
import uz.kuponbot.kupon.repository.OrderRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final ProductService productService;
    
    public Order createOrder(User user, List<OrderItem> items, String deliveryAddress, String customerName, String phoneNumber) {
        Order order = new Order();
        order.setUser(user);
        order.setOrderNumber(generateOrderNumber());
        order.setDeliveryAddress(deliveryAddress);
        order.setCustomerName(customerName);
        order.setPhoneNumber(phoneNumber);
        order.setStatus(Order.OrderStatus.PENDING);
        
        // Calculate total amount
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (OrderItem item : items) {
            item.setOrder(order);
            item.setTotalPrice(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            totalAmount = totalAmount.add(item.getTotalPrice());
        }
        
        order.setTotalAmount(totalAmount);
        order.setOrderItems(items);
        
        Order savedOrder = orderRepository.save(order);
        
        // Update product stock
        for (OrderItem item : items) {
            productService.updateStock(item.getProduct().getId(), item.getQuantity());
        }
        
        return savedOrder;
    }
    
    public List<Order> getUserOrders(User user) {
        return orderRepository.findByUserOrderByCreatedAtDesc(user);
    }
    
    public Optional<Order> findByOrderNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber);
    }
    
    public Order updateOrderStatus(Long orderId, Order.OrderStatus status) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            order.setStatus(status);
            return orderRepository.save(order);
        }
        return null;
    }
    
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
    
    public List<Order> getOrdersByStatus(Order.OrderStatus status) {
        return orderRepository.findByStatusOrderByCreatedAtDesc(status);
    }
    
    public long getTotalOrdersCount() {
        return orderRepository.count();
    }
    
    public long getDeliveredOrdersCount() {
        return orderRepository.countDeliveredOrders();
    }
    
    public long getActiveOrdersCount() {
        return orderRepository.countActiveOrders();
    }
    
    private String generateOrderNumber() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return "ORD" + timestamp;
    }
}