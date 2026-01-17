# Kupon Bot - Telegram Bot

Bu loyiha Telegram orqali kupon tizimini boshqarish uchun yaratilgan bot hisoblanadi.

## Xususiyatlar

### Foydalanuvchi uchun:
- 📱 Telefon raqami orqali ro'yxatdan o'tish
- 👤 Ism va familiya kiritish
- 🎫 Avtomatik kupon kodi olish (5 ta harf va raqamdan iborat)
- 📋 O'z kuponlarini ko'rish
- 🆕 Yangi kupon yaratish
- 👤 Profil ma'lumotlarini ko'rish

### Admin uchun:
- 📊 Umumiy statistikalar
- 👥 Barcha foydalanuvchilarni ko'rish
- 🎫 Barcha kuponlarni boshqarish
- 🔍 Qidiruv funksiyasi
- ✅ Kuponlarni ishlatish

## Texnologiyalar

- **Backend**: Spring Boot 4.0.1, Java 21
- **Database**: PostgreSQL
- **Telegram API**: TelegramBots 6.8.0
- **Frontend**: HTML, CSS, JavaScript (Admin Panel)

## O'rnatish

### 1. Talablar
- Java 21+
- PostgreSQL
- Maven

### 2. Database sozlash
```sql
CREATE DATABASE kuponbot;
CREATE USER kuponbot_user WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE kuponbot TO kuponbot_user;
```

### 3. Konfiguratsiya
`src/main/resources/application.properties` faylida quyidagi sozlamalarni o'zgartiring:

```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/kuponbot
spring.datasource.username=kuponbot_user
spring.datasource.password=your_password

# Telegram Bot
telegram.bot.token=YOUR_BOT_TOKEN_HERE
telegram.bot.username=YOUR_BOT_USERNAME_HERE
```

### 4. Telegram Bot yaratish
1. [@BotFather](https://t.me/botfather) ga murojaat qiling
2. `/newbot` buyrug'ini yuboring
3. Bot nomini va username'ini kiriting
4. Olingan token'ni `application.properties` ga qo'ying

### 5. Loyihani ishga tushirish
```bash
mvn clean install
mvn spring-boot:run
```

## Foydalanish

### Foydalanuvchi uchun:
1. Telegram'da botni toping
2. `/start` buyrug'ini yuboring
3. Telefon raqamingizni yuboring
4. Ism va familiyangizni kiriting
5. Kupon kodingizni oling

### Admin uchun:
**Login sahifa**: `http://localhost:8080/login.html`  
**Admin panel**: `http://localhost:8080/admin.html`

#### Admin bo'lish:
1. Bot'ga `/myid` yuboring - Telegram ID'ingizni oling
2. `application.properties` faylida `admin.telegram.ids` ga ID'ingizni qo'ying  
3. Bot'ga `/admin` yuboring - Admin kodini oling
4. Login sahifada kodni kiriting

#### Admin: [@IbodullaR](https://t.me/IbodullaR)

## API Endpoints

### Admin API:
- `GET /api/admin/stats` - Umumiy statistikalar
- `GET /api/admin/users` - Barcha foydalanuvchilar
- `GET /api/admin/coupons` - Barcha kuponlar
- `GET /api/admin/users/{userId}/coupons` - Foydalanuvchi kuponlari
- `GET /api/admin/coupons/{code}` - Kupon ma'lumotlari
- `PUT /api/admin/coupons/{code}/use` - Kuponni ishlatish

## Loyiha strukturasi

```
src/
├── main/
│   ├── java/uz/kuponbot/kupon/
│   │   ├── bot/           # Telegram bot
│   │   ├── config/        # Konfiguratsiya
│   │   ├── controller/    # REST API
│   │   ├── dto/          # Data Transfer Objects
│   │   ├── entity/       # JPA Entities
│   │   ├── repository/   # Data Access Layer
│   │   └── service/      # Business Logic
│   └── resources/
│       ├── static/       # Admin Panel (HTML/CSS/JS)
│       └── application.properties
```

## Xavfsizlik

- Kupon kodlari kriptografik jihatdan xavfsiz generatsiya qilinadi
- Har bir kupon kodi noyob (unique)
- Database'da barcha ma'lumotlar xavfsiz saqlanadi

## Kelajakdagi rivojlanish

- [ ] Kupon amal qilish muddati
- [ ] Kupon turlari (chegirma, bepul mahsulot, etc.)
- [ ] Email orqali xabar yuborish
- [ ] Kupon statistikalari
- [ ] Bulk kupon yaratish
- [ ] QR kod generatsiyasi

## Yordam

Savollar yoki muammolar bo'lsa, GitHub Issues bo'limida murojaat qiling.