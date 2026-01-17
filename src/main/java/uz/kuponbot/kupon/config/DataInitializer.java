package uz.kuponbot.kupon.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import uz.kuponbot.kupon.entity.Product;
import uz.kuponbot.kupon.entity.User;
import uz.kuponbot.kupon.service.ProductService;
import uz.kuponbot.kupon.service.UserService;
import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    
    private final ProductService productService;
    private final UserService userService;
    
    @Override
    public void run(String... args) throws Exception {
        // Initialize sample products if none exist
        if (productService.getAllProducts().isEmpty()) {
            createSampleProducts();
        }
        
        // Create test user if not exists
        if (userService.findByTelegramId(1807166165L).isEmpty()) {
            createTestUser();
        }
    }
    
    private void createSampleProducts() {
        // Create sample glasses products
        productService.createProduct(
            "Ray-Ban Aviator Classic",
            "Klassik aviator uslubidagi ko'zoynak. UV himoyasi bilan.",
            "450000",
            "https://images.unsplash.com/photo-1511499767150-a48a237f0083?w=300&h=200&fit=crop",
            15
        );
        
        productService.createProduct(
            "Oakley Holbrook Sport",
            "Sport uslubidagi ko'zoynak. Faol hayot tarzi uchun ideal.",
            "380000",
            "https://images.unsplash.com/photo-1574258495973-f010dfbb5371?w=300&h=200&fit=crop",
            20
        );
        
        productService.createProduct(
            "Persol Premium Collection",
            "Premium sifatli italyan ko'zoynagi. Zamonaviy dizayn.",
            "650000",
            "https://images.unsplash.com/photo-1556306535-38febf6782e7?w=300&h=200&fit=crop",
            10
        );
        
        productService.createProduct(
            "Gucci Designer Frames",
            "Brendli dizayner ko'zoynagi. Moda va sifat.",
            "850000",
            "https://images.unsplash.com/photo-1508296695146-257a814070b4?w=300&h=200&fit=crop",
            8
        );
        
        productService.createProduct(
            "Prada Luxury Eyewear",
            "Hashamatli Prada ko'zoynagi. Yuqori sifat kafolati.",
            "920000",
            "https://images.unsplash.com/photo-1577803645773-f96470509666?w=300&h=200&fit=crop",
            5
        );
        
        System.out.println("✅ Sample products created successfully!");
    }
    
    private void createTestUser() {
        User testUser = new User();
        testUser.setTelegramId(1807166165L);
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setPhoneNumber("+998901234567");
        testUser.setState(User.UserState.REGISTERED);
        
        userService.save(testUser);
        System.out.println("✅ Test user created successfully!");
    }
}