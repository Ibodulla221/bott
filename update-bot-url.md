# Bot'da URL yangilash

Vercel domain olgandan keyin, bot kodida quyidagini o'zgartiring:

```java
// Eski:
shopButton.setUrl("http://localhost:8080/shop.html");

// Yangi (sizning domain bilan):
shopButton.setUrl("https://your-project.vercel.app/shop.html");
```

Keyin bot'ni qayta ishga tushiring.