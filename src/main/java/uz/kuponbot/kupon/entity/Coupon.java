package uz.kuponbot.kupon.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "coupons")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Coupon {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false, length = 5)
    private String code;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Enumerated(EnumType.STRING)
    private CouponStatus status = CouponStatus.ACTIVE;
    
    private LocalDateTime createdAt = LocalDateTime.now();
    
    private LocalDateTime usedAt;
    
    public enum CouponStatus {
        ACTIVE,
        USED,
        EXPIRED
    }
}