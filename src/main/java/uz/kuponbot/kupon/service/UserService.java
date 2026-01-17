package uz.kuponbot.kupon.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.kuponbot.kupon.entity.User;
import uz.kuponbot.kupon.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    
    public Optional<User> findByTelegramId(Long telegramId) {
        return userRepository.findByTelegramId(telegramId);
    }
    
    public User save(User user) {
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }
    
    public User createUser(Long telegramId) {
        User user = new User();
        user.setTelegramId(telegramId);
        user.setState(User.UserState.WAITING_CONTACT);
        return save(user);
    }
    
    public boolean existsByTelegramId(Long telegramId) {
        return userRepository.existsByTelegramId(telegramId);
    }
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public long getTotalUsersCount() {
        return userRepository.count();
    }
}