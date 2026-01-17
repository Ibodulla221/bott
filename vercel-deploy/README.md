# Ko'zoynak Do'koni - Telegram Mini App

## Deploy qilish:

1. **Vercel.com** ga boring va GitHub bilan ro'yxatdan o'ting
2. Bu papkani GitHub repository'ga yuklang
3. Vercel'da "New Project" → GitHub repo'ni tanlang
4. Deploy tugmasini bosing
5. Avtomatik HTTPS domain olasiz: `https://your-project.vercel.app`

## Yoki tezkor deploy:

1. Vercel CLI o'rnating: `npm i -g vercel`
2. Bu papkada: `vercel --prod`
3. Domain olasiz

## Bot'da URL yangilash:

Bot kodida `localhost:8080` o'rniga Vercel domain'ni qo'ying:
```java
shopButton.setUrl("https://your-project.vercel.app/shop.html");
```

## Test qilish:

1. Admin panel: `https://your-domain.vercel.app/admin.html`
2. Do'kon: `https://your-domain.vercel.app/shop.html`
3. Login: `https://your-domain.vercel.app/login.html`