package uz.kuponbot.kupon.bot;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uz.kuponbot.kupon.entity.Coupon;
import uz.kuponbot.kupon.entity.User;
import uz.kuponbot.kupon.service.CouponService;
import uz.kuponbot.kupon.service.UserService;

@Component
@RequiredArgsConstructor
@Slf4j
public class KuponBot extends TelegramLongPollingBot {
    
    private final UserService userService;
    private final CouponService couponService;
    
    @Value("${telegram.bot.token}")
    private String botToken;
    
    @Value("${telegram.bot.username}")
    private String botUsername;
    
    @Override
    public String getBotToken() {
        return botToken;
    }
    
    @Override
    public String getBotUsername() {
        return botUsername;
    }
    
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            Long chatId = message.getChatId();
            Long userId = message.getFrom().getId();
            
            try {
                handleMessage(message, chatId, userId);
            } catch (Exception e) {
                log.error("Error processing message: ", e);
                sendMessage(chatId, "Xatolik yuz berdi. Iltimos, qaytadan urinib ko'ring.");
            }
        }
    }
    
    private void handleMessage(Message message, Long chatId, Long userId) {
        Optional<User> userOpt = userService.findByTelegramId(userId);
        
        if (userOpt.isEmpty()) {
            // Yangi foydalanuvchi
            User newUser = userService.createUser(userId);
            sendWelcomeMessage(chatId);
            return;
        }
        
        User user = userOpt.get();
        
        switch (user.getState()) {
            case WAITING_CONTACT -> handleContactState(message, user, chatId);
            case WAITING_FIRST_NAME -> handleFirstNameState(message, user, chatId);
            case WAITING_LAST_NAME -> handleLastNameState(message, user, chatId);
            case REGISTERED -> handleRegisteredUserCommands(message, user, chatId);
            default -> sendWelcomeMessage(chatId);
        }
    }
    
    private void sendWelcomeMessage(Long chatId) {
        String welcomeText = "🎉 Kupon botiga xush kelibsiz!\n\n" +
                "Ro'yxatdan o'tish uchun telefon raqamingizni yuboring.";
        
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(welcomeText);
        sendMessage.setReplyMarkup(createContactKeyboard());
        
        sendMessage(sendMessage);
    }
    
    private ReplyKeyboardMarkup createContactKeyboard() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);
        
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        
        KeyboardButton contactButton = new KeyboardButton();
        contactButton.setText("📱 Telefon raqamni yuborish");
        contactButton.setRequestContact(true);
        
        row.add(contactButton);
        keyboard.add(row);
        keyboardMarkup.setKeyboard(keyboard);
        
        return keyboardMarkup;
    }
    
    private void handleContactState(Message message, User user, Long chatId) {
        if (message.hasContact()) {
            Contact contact = message.getContact();
            user.setPhoneNumber(contact.getPhoneNumber());
            user.setState(User.UserState.WAITING_FIRST_NAME);
            userService.save(user);
            
            sendMessage(chatId, "✅ Telefon raqam qabul qilindi!\n\nEndi ismingizni kiriting:");
        } else {
            sendMessage(chatId, "❌ Iltimos, telefon raqamingizni yuborish tugmasini bosing.");
        }
    }
    
    private void handleFirstNameState(Message message, User user, Long chatId) {
        if (message.hasText()) {
            String firstName = message.getText().trim();
            if (firstName.length() >= 2) {
                user.setFirstName(firstName);
                user.setState(User.UserState.WAITING_LAST_NAME);
                userService.save(user);
                
                sendMessage(chatId, "✅ Ism qabul qilindi!\n\nEndi familiyangizni kiriting:");
            } else {
                sendMessage(chatId, "❌ Ism kamida 2 ta harfdan iborat bo'lishi kerak.");
            }
        } else {
            sendMessage(chatId, "❌ Iltimos, ismingizni matn ko'rinishida yuboring.");
        }
    }
    
    private void handleLastNameState(Message message, User user, Long chatId) {
        if (message.hasText()) {
            String lastName = message.getText().trim();
            if (lastName.length() >= 2) {
                user.setLastName(lastName);
                user.setState(User.UserState.REGISTERED);
                userService.save(user);
                
                // Kupon yaratish
                Coupon coupon = couponService.createCouponForUser(user);
                
                String successMessage = String.format(
                    "🎉 Tabriklaymiz! Ro'yxatdan o'tish muvaffaqiyatli yakunlandi!\n\n" +
                    "👤 Ism: %s\n" +
                    "👤 Familiya: %s\n" +
                    "📱 Telefon: %s\n\n" +
                    "🎫 Sizning kupon kodingiz: *%s*\n\n" +
                    "Bu kodni saqlang va kerak bo'lganda ishlatishingiz mumkin!",
                    user.getFirstName(), user.getLastName(), user.getPhoneNumber(), coupon.getCode()
                );
                
                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(chatId);
                sendMessage.setText(successMessage);
                sendMessage.setParseMode("Markdown");
                sendMessage.setReplyMarkup(createMainMenuKeyboard());
                
                sendMessage(sendMessage);
            } else {
                sendMessage(chatId, "❌ Familiya kamida 2 ta harfdan iborat bo'lishi kerak.");
            }
        } else {
            sendMessage(chatId, "❌ Iltimos, familiyangizni matn ko'rinishida yuboring.");
        }
    }
    
    private ReplyKeyboardMarkup createMainMenuKeyboard() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        
        List<KeyboardRow> keyboard = new ArrayList<>();
        
        KeyboardRow row1 = new KeyboardRow();
        row1.add("🛒 Do'kon");
        row1.add("📦 Buyurtmalarim");
        
        KeyboardRow row2 = new KeyboardRow();
        row2.add("👤 Profil");
        row2.add("ℹ️ Yordam");
        
        keyboard.add(row1);
        keyboard.add(row2);
        keyboardMarkup.setKeyboard(keyboard);
        
        return keyboardMarkup;
    }
    
    private void handleRegisteredUserCommands(Message message, User user, Long chatId) {
        if (!message.hasText()) {
            return;
        }
        
        String text = message.getText();
        
        switch (text) {
            case "🛒 Do'kon" -> openShop(chatId);
            case "📦 Buyurtmalarim" -> showUserOrders(user, chatId);
            case "👤 Profil" -> showUserProfile(user, chatId);
            case "ℹ️ Yordam" -> showHelp(chatId);
            case "/start" -> sendRegisteredUserWelcome(user, chatId);
            case "/admin" -> handleAdminCommand(user, chatId);
            case "/myid" -> sendMessage(chatId, "🆔 Sizning Telegram ID: " + user.getTelegramId());
            default -> sendMessage(chatId, "❌ Noma'lum buyruq. Iltimos, menyudan tanlang.");
        }
    }
    
    private void showUserCoupons(User user, Long chatId) {
        List<Coupon> coupons = couponService.getUserCoupons(user);
        
        if (coupons.isEmpty()) {
            sendMessage(chatId, "❌ Sizda hozircha kuponlar yo'q.");
            return;
        }
        
        StringBuilder message = new StringBuilder("🎫 Sizning kuponlaringiz:\n\n");
        
        for (int i = 0; i < coupons.size(); i++) {
            Coupon coupon = coupons.get(i);
            String status = coupon.getStatus() == Coupon.CouponStatus.ACTIVE ? "✅ Faol" : "❌ Ishlatilgan";
            message.append(String.format("%d. Kod: *%s* - %s\n", i + 1, coupon.getCode(), status));
        }
        
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(message.toString());
        sendMessage.setParseMode("Markdown");
        
        sendMessage(sendMessage);
    }
    
    private void generateNewCoupon(User user, Long chatId) {
        Coupon newCoupon = couponService.createCouponForUser(user);
        
        String message = String.format(
            "🎉 Yangi kupon yaratildi!\n\n🎫 Kupon kodi: *%s*\n\nBu kodni saqlang!",
            newCoupon.getCode()
        );
        
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);
        sendMessage.setParseMode("Markdown");
        
        sendMessage(sendMessage);
    }
    
    private void showUserProfile(User user, Long chatId) {
        List<Coupon> userCoupons = couponService.getUserCoupons(user);
        long activeCoupons = userCoupons.stream()
            .filter(c -> c.getStatus() == Coupon.CouponStatus.ACTIVE)
            .count();
        
        String profileMessage = String.format(
            "👤 Sizning profilingiz:\n\n" +
            "📝 Ism: %s\n" +
            "📝 Familiya: %s\n" +
            "📱 Telefon: %s\n" +
            "🎫 Jami kuponlar: %d\n" +
            "✅ Faol kuponlar: %d\n" +
            "📅 Ro'yxatdan o'tgan: %s",
            user.getFirstName(),
            user.getLastName(),
            user.getPhoneNumber(),
            userCoupons.size(),
            activeCoupons,
            user.getCreatedAt().toLocalDate()
        );
        
        sendMessage(chatId, profileMessage);
    }
    
    private void showHelp(Long chatId) {
        String helpMessage = """
            ℹ️ Yordam:
            
            🛒 Do'kon - ko'zoynaklar katalogini ko'rish va xarid qilish
            📦 Buyurtmalarim - buyurtmalar tarixi
            👤 Profil - shaxsiy ma'lumotlaringizni ko'rish
            ℹ️ Yordam - bu yordam xabari
            
            Savollar bo'lsa, admin bilan bog'laning.
            """;
        
        sendMessage(chatId, helpMessage);
    }
    
    private void openShop(Long chatId) {
        String shopMessage = """
            🛒 Ko'zoynak Do'koni
            
            Bizning do'konimizda eng sifatli ko'zoynaklar mavjud!
            
            Do'konni ochish uchun quyidagi tugmani bosing:
            """;
        
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(shopMessage);
        
        // Create inline keyboard with web app button
        org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup inlineKeyboard = 
            new org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup();
        
        List<List<org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton> row = new ArrayList<>();
        
        org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton shopButton = 
            new org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton();
        shopButton.setText("🛒 Do'konni ochish");
        
        // For development, we'll use URL. For production, you need HTTPS domain for WebApp
        shopButton.setUrl("http://localhost:8080/shop.html");
        
        row.add(shopButton);
        keyboard.add(row);
        inlineKeyboard.setKeyboard(keyboard);
        
        sendMessage.setReplyMarkup(inlineKeyboard);
        sendMessage(sendMessage);
    }
    
    private void showUserOrders(User user, Long chatId) {
        // This will be implemented with OrderService
        String ordersMessage = """
            📦 Sizning buyurtmalaringiz:
            
            Hozircha buyurtmalar yo'q.
            
            Birinchi buyurtmangizni berish uchun do'konni oching!
            """;
        
        sendMessage(chatId, ordersMessage);
    }
    
    private void sendRegisteredUserWelcome(User user, Long chatId) {
        String welcomeMessage = String.format(
            "👋 Salom, %s!\n\nSiz allaqachon ro'yxatdan o'tgansiz. Menyudan kerakli bo'limni tanlang.",
            user.getFirstName()
        );
        
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(welcomeMessage);
        sendMessage.setReplyMarkup(createMainMenuKeyboard());
        
        sendMessage(sendMessage);
    }
    
    private void handleAdminCommand(User user, Long chatId) {
        // Admin Telegram ID'larini tekshirish (sizning ID'ingizni qo'yish kerak)
        Long adminTelegramId = 1807166165L; // Sizning haqiqiy Telegram ID'ingiz
        
        if (!user.getTelegramId().equals(adminTelegramId)) {
            sendMessage(chatId, "❌ Sizda admin huquqlari yo'q.");
            return;
        }
        
        String adminMessage = "🔐 Admin Panel\n\n" +
            "Admin panelga kirish uchun:\n" +
            "1. Brauzerda: http://localhost:8080/login.html\n" +
            "2. Admin kodi: ADMIN2024\n\n" +
            "📊 Tezkor statistika:\n" +
            "👥 Jami foydalanuvchilar: " + userService.getTotalUsersCount() + "\n" +
            "🎫 Jami kuponlar: " + couponService.getTotalCouponsCount() + "\n\n" +
            "Admin: @IbodullaR";
        
        sendMessage(chatId, adminMessage);
    }
    
    private void sendMessage(Long chatId, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        sendMessage(sendMessage);
    }
    
    private void sendMessage(SendMessage sendMessage) {
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Error sending message: ", e);
        }
    }
}