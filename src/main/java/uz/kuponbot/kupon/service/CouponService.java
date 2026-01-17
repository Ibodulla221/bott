package uz.kuponbot.kupon.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.kuponbot.kupon.entity.Coupon;
import uz.kuponbot.kupon.entity.User;
import uz.kuponbot.kupon.repository.CouponRepository;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CouponService {
    
    private final CouponRepository couponRepository;
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int COUPON_LENGTH = 5;
    private static final SecureRandom random = new SecureRandom();
    
    public String generateUniqueCouponCode() {
        String code;
        do {
            code = generateRandomCode();
        } while (couponRepository.existsByCode(code));
        return code;
    }
    
    private String generateRandomCode() {
        StringBuilder sb = new StringBuilder(COUPON_LENGTH);
        for (int i = 0; i < COUPON_LENGTH; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }
    
    public Coupon createCouponForUser(User user) {
        Coupon coupon = new Coupon();
        coupon.setCode(generateUniqueCouponCode());
        coupon.setUser(user);
        coupon.setStatus(Coupon.CouponStatus.ACTIVE);
        return couponRepository.save(coupon);
    }
    
    public List<Coupon> getUserCoupons(User user) {
        return couponRepository.findByUserOrderByCreatedAtDesc(user);
    }
    
    public Optional<Coupon> findByCode(String code) {
        return couponRepository.findByCode(code);
    }
    
    public List<Coupon> getAllCoupons() {
        return couponRepository.findAll();
    }
    
    public long getTotalCouponsCount() {
        return couponRepository.count();
    }
    
    public Coupon useCoupon(Coupon coupon) {
        coupon.setStatus(Coupon.CouponStatus.USED);
        coupon.setUsedAt(LocalDateTime.now());
        return couponRepository.save(coupon);
    }
}