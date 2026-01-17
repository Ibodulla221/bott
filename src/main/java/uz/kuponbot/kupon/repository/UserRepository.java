package uz.kuponbot.kupon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.kuponbot.kupon.entity.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByTelegramId(Long telegramId);
    
    boolean existsByTelegramId(Long telegramId);
}