# Ko'zoynak Shop - Deployment Guide

## Backend Deployment (Spring Boot)

The backend needs to be deployed to a cloud service that supports Java applications. Here are the recommended options:

### Option 1: Heroku (Recommended)
1. Create a Heroku account at https://heroku.com
2. Install Heroku CLI
3. Create a new Heroku app:
   ```bash
   heroku create your-app-name
   ```
4. Set environment variables:
   ```bash
   heroku config:set TELEGRAM_BOT_TOKEN=8567911447:AAGGOpjupFR9NyEsWmgHer9PFrnwb0n9q3E
   heroku config:set ADMIN_TELEGRAM_IDS=1807166165
   ```
5. Deploy:
   ```bash
   git add .
   git commit -m "Deploy to Heroku"
   git push heroku main
   ```

### Option 2: Railway
1. Go to https://railway.app
2. Connect your GitHub repository
3. Deploy automatically

### Option 3: Render
1. Go to https://render.com
2. Connect your GitHub repository
3. Create a new Web Service
4. Set build command: `./mvnw clean package`
5. Set start command: `java -jar target/kupon-0.0.1-SNAPSHOT.jar`

## Frontend Deployment (Already Done)

The frontend is already deployed to Vercel at: https://bott-ondv.vercel.app/

## After Backend Deployment

1. Get your backend URL (e.g., `https://your-app.herokuapp.com`)
2. Update the API configuration in both frontend files:
   - `vercel-deploy/shop.html`
   - `vercel-deploy/admin.html`
   
   Replace this line:
   ```javascript
   'https://your-backend-url.herokuapp.com'; // Replace with actual backend URL
   ```
   
   With your actual backend URL:
   ```javascript
   'https://your-app.herokuapp.com';
   ```

3. Redeploy the frontend to Vercel

## Database Configuration

The application currently uses H2 in-memory database for development. For production, you should:

1. Add PostgreSQL dependency to `pom.xml`
2. Update `application.properties` with production database settings
3. Most cloud providers offer free PostgreSQL databases

## Bot Configuration

Update the bot's webhook URL to point to your deployed backend:
```
https://api.telegram.org/bot8567911447:AAGGOpjupFR9NyEsWmgHer9PFrnwb0n9q3E/setWebhook?url=https://your-app.herokuapp.com/webhook
```

## Testing

1. Test the API endpoints:
   - `GET https://your-app.herokuapp.com/api/shop/products`
   - `POST https://your-app.herokuapp.com/api/shop/orders`

2. Test the Telegram bot
3. Test the Mini App at https://bott-ondv.vercel.app/

## Current Status

✅ **COMPLETED:**
- Complete checkout system with cart functionality
- Payment method selection (Electronic/Cash on Delivery)
- Order creation and database storage
- Admin panel with order management
- Order notifications (console logs)
- CORS configuration fixed
- Sample data initialization
- Mini App deployment to Vercel

✅ **WORKING FEATURES:**
- Product catalog loading
- Add to cart functionality
- Customer information form
- Payment method selection
- Order creation and confirmation
- Admin panel authentication
- Order viewing in admin panel
- Real-time order notifications

🔧 **NEXT STEPS:**
1. Deploy backend to production (Heroku/Railway/Render)
2. Update frontend API URLs
3. Configure production database
4. Set up Telegram webhook
5. Add real Telegram notifications to admin