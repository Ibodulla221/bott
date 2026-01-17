package uz.kuponbot.kupon.config;

import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdminConfig {
    
    @Value("${admin.telegram.ids:}")
    private Set<Long> adminTelegramIds;
    
    public boolean isAdmin(Long telegramId) {
        return adminTelegramIds.contains(telegramId);
    }
    
    public Set<Long> getAdminIds() {
        return adminTelegramIds;
    }
}