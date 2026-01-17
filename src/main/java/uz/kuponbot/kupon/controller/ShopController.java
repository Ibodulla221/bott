package uz.kuponbot.kupon.controller;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.kuponbot.kupon.dto.*;
import uz.kuponbot.kupon.entity.*;
import uz.kuponbot.kupon.service.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/shop")
@RequiredArgsConstructor
public class ShopController {
    
    private final ProductService productService;
    private final OrderService orderService;
    private final UserService userService;
    
    @GetMapping("/products")
    public ResponseEntity<List<ProductDto>> getProducts() {
        List<Product> products = productService.getAvailableProducts();
        List<ProductDto> productDtos = products.stream()
            .map(this::convertToProductDto)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(productDtos);
    }
    
    @GetMapping("/products/{id}")
    public ResponseEntity<ProductDto> getProduct(@PathVariable Long id) {
        Optional<Product> productOpt = productService.findById(id);
        if (productOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(convertToProductDto(productOpt.get()));
    }
    
    @PostMapping("/orders")
    public ResponseEntity<OrderDto> createOrder(@RequestBody CreateOrderRequest request) {
        Optional<User> userOpt = userService.findByTelegramId(request.getUserId());
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        User user = userOpt.get();
        List<OrderItem> orderItems = new ArrayList<>();
        
        for (CreateOrderRequest.OrderItemRequest itemRequest : request.getItems()) {
            Optional<Product> productOpt = productService.findById(itemRequest.getProductId());
            if (productOpt.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            
            Product product = productOpt.get();
            if (product.getStockQuantity() < itemRequest.getQuantity()) {
                return ResponseEntity.badRequest().build();
            }
            
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setUnitPrice(product.getPrice());
            
            orderItems.add(orderItem);
        }
        
        Order order = orderService.createOrder(
            user, 
            orderItems, 
            request.getDeliveryAddress(),
            request.getCustomerName(),
            request.getPhoneNumber()
        );
        
        // Send notification to admin via Telegram
        sendOrderNotificationToAdmin(order);
        
        return ResponseEntity.ok(convertToOrderDto(order));
    }
    
    private void sendOrderNotificationToAdmin(Order order) {
        try {
            // This would need to be implemented with a Telegram bot service
            // For now, we'll log it
            System.out.println("🔔 Yangi buyurtma: " + order.getOrderNumber());
            System.out.println("👤 Mijoz: " + order.getCustomerName());
            System.out.println("📱 Telefon: " + order.getPhoneNumber());
            System.out.println("💰 Summa: " + order.getTotalAmount() + " so'm");
        } catch (Exception e) {
            System.err.println("Error sending notification: " + e.getMessage());
        }
    }
    
    @GetMapping("/orders/user/{userId}")
    public ResponseEntity<List<OrderDto>> getUserOrders(@PathVariable Long userId) {
        Optional<User> userOpt = userService.findByTelegramId(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        List<Order> orders = orderService.getUserOrders(userOpt.get());
        List<OrderDto> orderDtos = orders.stream()
            .map(this::convertToOrderDto)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(orderDtos);
    }
    
    @GetMapping("/orders/{orderNumber}")
    public ResponseEntity<OrderDto> getOrder(@PathVariable String orderNumber) {
        Optional<Order> orderOpt = orderService.findByOrderNumber(orderNumber);
        if (orderOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(convertToOrderDto(orderOpt.get()));
    }
    
    private ProductDto convertToProductDto(Product product) {
        return new ProductDto(
            product.getId(),
            product.getName(),
            product.getDescription(),
            product.getPrice(),
            product.getImageUrl(),
            product.getStockQuantity(),
            product.getStatus().toString(),
            product.getCreatedAt()
        );
    }
    
    private OrderDto convertToOrderDto(Order order) {
        List<OrderItemDto> itemDtos = order.getOrderItems().stream()
            .map(this::convertToOrderItemDto)
            .collect(Collectors.toList());
        
        return new OrderDto(
            order.getId(),
            order.getOrderNumber(),
            order.getUser().getTelegramId(),
            order.getCustomerName(),
            order.getPhoneNumber(),
            order.getDeliveryAddress(),
            order.getTotalAmount(),
            order.getStatus().toString(),
            order.getNotes(),
            itemDtos,
            order.getCreatedAt(),
            order.getUpdatedAt()
        );
    }
    
    private OrderItemDto convertToOrderItemDto(OrderItem item) {
        return new OrderItemDto(
            item.getId(),
            item.getProduct().getId(),
            item.getProduct().getName(),
            item.getProduct().getImageUrl(),
            item.getQuantity(),
            item.getUnitPrice(),
            item.getTotalPrice()
        );
    }
    
    @Data
    public static class CreateOrderRequest {
        private Long userId;
        private String customerName;
        private String phoneNumber;
        private String deliveryAddress;
        private String notes;
        private List<OrderItemRequest> items;
        
        @Data
        public static class OrderItemRequest {
            private Long productId;
            private Integer quantity;
        }
    }
}