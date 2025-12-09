# Network Setup Guide for Expo Go Development

## Current Configuration

### Your Machine's Local IP
- **IP Address**: `10.114.195.96`
- **Backend Port**: `8080`
- **Backend URL**: `http://10.114.195.96:8080`

### Frontend Configuration
The frontend is now configured to use your local network IP instead of localhost:
- **File**: `frontend/.env`
- **API URL**: `http://10.114.195.96:8080`

### Backend Configuration
The backend is configured to:
1. **Listen on all network interfaces** (0.0.0.0) - allows connections from local network
2. **Accept CORS requests** from local network IPs (10.*, 192.168.*, 172.*)
3. **Run on port 8080**

## Setup Steps

### 1. Start the Backend
```bash
cd backend
mvn spring-boot:run
```

The backend will be accessible at:
- From your computer: `http://localhost:8080`
- From your phone/device: `http://10.114.195.96:8080`

### 2. Start the Frontend (Expo)
```bash
cd frontend
npm start
# or
npx expo start
```

### 3. Connect with Expo Go
1. Install **Expo Go** app on your physical device
2. Make sure your phone is on the **same Wi-Fi network** as your computer
3. Scan the QR code shown in the terminal/browser
4. The app should connect to the backend at `http://10.114.195.96:8080`

## Troubleshooting

### If API calls fail with network errors:

1. **Check both devices are on the same Wi-Fi network**
   - Your computer and phone must be on the same network
   - Some public/corporate Wi-Fi networks block device-to-device communication

2. **Verify your IP hasn't changed**
   ```bash
   ifconfig | grep "inet " | grep -v 127.0.0.1
   ```
   - If your IP changed, update `frontend/.env` with the new IP
   - Restart the Expo server after changing the IP

3. **Check firewall settings**
   - macOS: System Preferences → Security & Privacy → Firewall
   - Allow incoming connections for Java/Terminal

4. **Test backend connectivity**
   - From your phone's browser, visit: `http://10.114.195.96:8080/actuator/health`
   - You should see a health status JSON response

### If you still get CORS errors:

The backend is configured to allow requests from local network IPs in dev mode:
- `http://localhost:*`
- `http://10.*`
- `http://192.168.*`
- `http://172.*`

If you need to add more patterns, edit:
- `backend/src/main/resources/application.yml` (line 102)

## Alternative: Using ngrok

If the above doesn't work (e.g., network restrictions), you can use ngrok:

1. Install ngrok: `brew install ngrok`
2. Start your backend on localhost:8080
3. Run: `ngrok http 8080`
4. Update `frontend/.env` with the ngrok URL (e.g., `https://abc123.ngrok.io`)
5. Update CORS in `application.yml` to allow the ngrok domain

## Production Deployment

For production, remember to:
1. Update `frontend/.env.production` with your actual API URL
2. Update `application.yml` prod profile with your production domain
3. Use HTTPS for all connections
4. Restrict CORS to your specific frontend domain
