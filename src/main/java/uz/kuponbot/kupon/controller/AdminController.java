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

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminController {
    
    private final UserService userService;
    private final CouponService couponService;
    private final ProductService productService;
    private final OrderService orderService;
    
    @GetMapping("/stats")
    public ResponseEntity<AdminStatsDto> getStats() {
        long totalUsers = userService.getTotalUsersCount();
        long totalCoupons = couponService.getTotalCouponsCount();
        long totalProducts = productService.getTotalProductsCount();
        long totalOrders = orderService.getTotalOrdersCount();
        
        List<Coupon> allCoupons = couponService.getAllCoupons();
        long activeCoupons = allCoupons.stream()
            .filter(c -> c.getStatus() == Coupon.CouponStatus.ACTIVE)
            .count();
        long usedCoupons = allCoupons.stream()
            .filter(c -> c.getStatus() == Coupon.CouponStatus.USED)
            .count();
        
        AdminStatsDto stats = new AdminStatsDto(
            totalUsers,
            totalCoupons,
            activeCoupons,
            usedCoupons,
            totalProducts,
            totalOrders
        );
        
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        List<UserDto> userDtos = users.stream()
            .map(this::convertToUserDto)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(userDtos);
    }
    
    @GetMapping("/products")
    public ResponseEntity<List<ProductDto>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        List<ProductDto> productDtos = products.stream()
            .map(this::convertToProductDto)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(productDtos);
    }
    
    @PostMapping("/products")
    public ResponseEntity<ProductDto> createProduct(@RequestBody CreateProductRequest request) {
        Product product = productService.createProduct(
            request.getName(),
            request.getDescription(),
            request.getPrice(),
            request.getImageUrl(),
            request.getStockQuantity()
        );
        
        return ResponseEntity.ok(convertToProductDto(product));
    }
    
    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/orders")
    public ResponseEntity<List<OrderDto>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        List<OrderDto> orderDtos = orders.stream()
            .map(this::convertToOrderDto)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(orderDtos);
    }
    
    @PutMapping("/orders/{id}/status")
    public ResponseEntity<OrderDto> updateOrderStatus(@PathVariable Long id, @RequestBody UpdateOrderStatusRequest request) {
        Order order = orderService.updateOrderStatus(id, Order.OrderStatus.valueOf(request.getStatus()));
        if (order == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(convertToOrderDto(order));
    }
    
    private UserDto convertToUserDto(User user) {
        List<Coupon> userCoupons = couponService.getUserCoupons(user);
        long activeCoupons = userCoupons.stream()
            .filter(c -> c.getStatus() == Coupon.CouponStatus.ACTIVE)
            .count();
        
        return new UserDto(
            user.getId(),
            user.getTelegramId(),
            user.getFirstName(),
            user.getLastName(),
            user.getPhoneNumber(),
            user.getState().toString(),
            user.getCreatedAt(),
            userCoupons.size(),
            activeCoupons
        );
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
    public static class CreateProductRequest {
        private String name;
        private String description;
        private String price;
        private String imageUrl;
        private Integer stockQuantity;
    }
    
    @Data
    public static class UpdateOrderStatusRequest {
        private String status;
    }
}