package uz.kuponbot.kupon;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.ActiveProfiles;
import uz.kuponbot.kupon.config.BotConfig;

@SpringBootTest
@ActiveProfiles("test")
@ComponentScan(excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = BotConfig.class))
class KuponApplicationTests {

    @Test
    void contextLoads() {
    }

}
