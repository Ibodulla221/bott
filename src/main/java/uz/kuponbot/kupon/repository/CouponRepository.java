package uz.kuponbot.kupon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.kuponbot.kupon.entity.Coupon;
import uz.kuponbot.kupon.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {
    
    Optional<Coupon> findByCode(String code);
    
    List<Coupon> findByUser(User user);
    
    List<Coupon> findByUserOrderByCreatedAtDesc(User user);
    
    boolean existsByCode(String code);
}