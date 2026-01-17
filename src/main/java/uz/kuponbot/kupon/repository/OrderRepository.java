package uz.kuponbot.kupon.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import uz.kuponbot.kupon.entity.Order;
import uz.kuponbot.kupon.entity.User;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    List<Order> findByUserOrderByCreatedAtDesc(User user);
    
    Optional<Order> findByOrderNumber(String orderNumber);
    
    List<Order> findByStatusOrderByCreatedAtDesc(Order.OrderStatus status);
    
    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = 'DELIVERED'")
    long countDeliveredOrders();
    
    @Query("SELECT COUNT(o) FROM Order o WHERE o.status IN ('PENDING', 'CONFIRMED', 'PREPARING', 'SHIPPED')")
    long countActiveOrders();
}