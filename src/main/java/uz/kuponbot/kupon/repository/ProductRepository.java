package uz.kuponbot.kupon.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import uz.kuponbot.kupon.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    List<Product> findByStatusOrderByCreatedAtDesc(Product.ProductStatus status);
    
    @Query("SELECT p FROM Product p WHERE p.status = 'ACTIVE' AND p.stockQuantity > 0 ORDER BY p.createdAt DESC")
    List<Product> findAvailableProducts();
    
    @Query("SELECT COUNT(p) FROM Product p WHERE p.status = 'ACTIVE'")
    long countActiveProducts();
}