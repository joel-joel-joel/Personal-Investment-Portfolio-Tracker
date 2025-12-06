# Personal Investment Portfolio Tracker - API Documentation

**Base URL:** `http://localhost:8080` (Development) or your deployed backend URL

**API Version:** v1

---

## Table of Contents

1. [Authentication & Headers](#authentication--headers)
2. [Stock Endpoints](#stock-endpoints)
3. [News Endpoints](#news-endpoints)
4. [Sector Mapping Information](#sector-mapping-information)
5. [Error Handling](#error-handling)
6. [Response Schemas](#response-schemas)

---

## Authentication & Headers

### JWT Authentication

**All protected endpoints** require a JWT token in the `Authorization` header.

```
Authorization: Bearer <jwt_token>
```

**Public endpoints** (marked below):
- FinnHub stock data endpoints (quotes, profiles)
- News endpoints (can be accessed without authentication)

### CORS Configuration

The backend is configured with CORS enabled for all origins (`*`). You can make requests directly from your React/React Native frontend.

### Standard Headers

For all requests, include:

```
Content-Type: application/json
```

---

## Stock Endpoints

### 1. Get All Stocks

**Endpoint:** `GET /api/stocks`

**Authentication:** Required (JWT)

**Description:** Retrieve all stocks from the portfolio tracker.

**Request:**
```
GET /api/stocks
Authorization: Bearer <jwt_token>
```

**Query Parameters:** None

**Response: 200 OK**
```json
[
  {
    "stockId": "550e8400-e29b-41d4-a716-446655440000",
    "stockCode": "AAPL",
    "companyName": "Apple Inc.",
    "stockValue": 195.50
  },
  {
    "stockId": "550e8400-e29b-41d4-a716-446655440001",
    "stockCode": "MSFT",
    "companyName": "Microsoft Corporation",
    "stockValue": 420.75
  }
]
```

**Error Responses:**
- `401 Unauthorized` - Invalid or missing JWT token
- `500 Internal Server Error` - Server error

---

### 2. Get Stock by ID

**Endpoint:** `GET /api/stocks/{id}`

**Authentication:** Required (JWT)

**Description:** Retrieve a specific stock by its UUID.

**Request:**
```
GET /api/stocks/550e8400-e29b-41d4-a716-446655440000
Authorization: Bearer <jwt_token>
```

**Path Parameters:**
- `id` (UUID) - The unique identifier of the stock

**Response: 200 OK**
```json
{
  "stockId": "550e8400-e29b-41d4-a716-446655440000",
  "stockCode": "AAPL",
  "companyName": "Apple Inc.",
  "stockValue": 195.50
}
```

**Error Responses:**
- `401 Unauthorized` - Invalid or missing JWT token
- `404 Not Found` - Stock with given ID does not exist
- `500 Internal Server Error` - Server error

---

### 3. Get Current Price for Stock

**Endpoint:** `GET /api/stocks/{id}/price`

**Authentication:** Required (JWT)

**Description:** Get the current price of a stock (fetched from FinnHub in real-time, fallback to database).

**Request:**
```
GET /api/stocks/550e8400-e29b-41d4-a716-446655440000/price
Authorization: Bearer <jwt_token>
```

**Path Parameters:**
- `id` (UUID) - The unique identifier of the stock

**Response: 200 OK**
```json
195.75
```

**Error Responses:**
- `401 Unauthorized` - Invalid or missing JWT token
- `404 Not Found` - Stock with given ID does not exist
- `500 Internal Server Error` - Server error (returns fallback database price if available)

---

### 4. Create Stock

**Endpoint:** `POST /api/stocks`

**Authentication:** Required (JWT)

**Description:** Create a new stock in the portfolio tracker.

**Request:**
```
POST /api/stocks
Authorization: Bearer <jwt_token>
Content-Type: application/json

{
  "stockCode": "AAPL",
  "companyName": "Apple Inc.",
  "stockValue": 195.50
}
```

**Request Body:**
- `stockCode` (string, required) - Stock ticker symbol (e.g., "AAPL"), must be unique
- `companyName` (string, required) - Full company name
- `stockValue` (decimal, optional) - Initial stock value (default: 0.00)

**Response: 200 OK**
```json
{
  "stockId": "550e8400-e29b-41d4-a716-446655440000",
  "stockCode": "AAPL",
  "companyName": "Apple Inc.",
  "stockValue": 195.50
}
```

**Error Responses:**
- `400 Bad Request` - Validation failed (e.g., duplicate stock code)
- `401 Unauthorized` - Invalid or missing JWT token
- `500 Internal Server Error` - Server error

---

### 5. Update Stock

**Endpoint:** `PUT /api/stocks/{id}`

**Authentication:** Required (JWT)

**Description:** Update an existing stock.

**Request:**
```
PUT /api/stocks/550e8400-e29b-41d4-a716-446655440000
Authorization: Bearer <jwt_token>
Content-Type: application/json

{
  "stockCode": "AAPL",
  "companyName": "Apple Inc.",
  "stockValue": 200.00
}
```

**Path Parameters:**
- `id` (UUID) - The unique identifier of the stock

**Request Body:**
- `stockCode` (string, required) - Stock ticker symbol
- `companyName` (string, required) - Full company name
- `stockValue` (decimal, optional) - Updated stock value

**Response: 200 OK**
```json
{
  "stockId": "550e8400-e29b-41d4-a716-446655440000",
  "stockCode": "AAPL",
  "companyName": "Apple Inc.",
  "stockValue": 200.00
}
```

**Error Responses:**
- `400 Bad Request` - Validation failed
- `401 Unauthorized` - Invalid or missing JWT token
- `404 Not Found` - Stock with given ID does not exist
- `500 Internal Server Error` - Server error

---

### 6. Delete Stock

**Endpoint:** `DELETE /api/stocks/{id}`

**Authentication:** Required (JWT)

**Description:** Delete a stock from the portfolio tracker.

**Request:**
```
DELETE /api/stocks/550e8400-e29b-41d4-a716-446655440000
Authorization: Bearer <jwt_token>
```

**Path Parameters:**
- `id` (UUID) - The unique identifier of the stock

**Response: 204 No Content**
```
(Empty response body)
```

**Error Responses:**
- `401 Unauthorized` - Invalid or missing JWT token
- `404 Not Found` - Stock with given ID does not exist
- `500 Internal Server Error` - Server error

---

### 7. Get Real-Time Stock Quote (FinnHub)

**Endpoint:** `GET /api/stocks/finnhub/quote/{symbol}`

**Authentication:** Not Required (Public)

**Description:** Get real-time stock quote from FinnHub API for any ticker symbol.

**Request:**
```
GET /api/stocks/finnhub/quote/AAPL
```

**Path Parameters:**
- `symbol` (string, required) - Stock ticker symbol (e.g., "AAPL", "MSFT")

**Response: 200 OK**
```json
{
  "currentPrice": 195.75,
  "highPrice": 198.50,
  "lowPrice": 192.10,
  "openPrice": 194.25,
  "previousClosePrice": 194.80,
  "timestamp": 1701953400
}
```

**Response Fields:**
- `currentPrice` (decimal) - Current stock price
- `highPrice` (decimal) - Day high price
- `lowPrice` (decimal) - Day low price
- `openPrice` (decimal) - Day opening price
- `previousClosePrice` (decimal) - Previous day closing price
- `timestamp` (long) - Unix timestamp of the quote

**Error Responses:**
- `400 Bad Request` - Invalid symbol or symbol not found
- `500 Internal Server Error` - FinnHub API error

---

### 8. Get Company Profile (FinnHub)

**Endpoint:** `GET /api/stocks/finnhub/profile/{symbol}`

**Authentication:** Not Required (Public)

**Description:** Get company profile information from FinnHub API for any ticker symbol.

**Request:**
```
GET /api/stocks/finnhub/profile/AAPL
```

**Path Parameters:**
- `symbol` (string, required) - Stock ticker symbol (e.g., "AAPL", "MSFT")

**Response: 200 OK**
```json
{
  "ticker": "AAPL",
  "companyName": "Apple Inc.",
  "industry": "technology",
  "marketCap": 3000000000000,
  "logo": "https://logo.com/aapl.png",
  "country": "US",
  "currency": "USD",
  "phone": "+1-408-996-1010",
  "website": "https://www.apple.com",
  "description": "Apple Inc. designs, manufactures, and markets smartphones, personal computers, tablets, wearables..."
}
```

**Response Fields:**
- `ticker` (string) - Stock ticker symbol
- `companyName` (string) - Full company name
- `industry` (string) - Industry classification
- `marketCap` (long) - Market capitalization in USD
- `logo` (string) - URL to company logo
- `country` (string) - Headquarters country code
- `currency` (string) - Trading currency
- `phone` (string) - Company phone number
- `website` (string) - Company website URL
- `description` (string) - Company description

**Error Responses:**
- `400 Bad Request` - Invalid symbol or symbol not found
- `500 Internal Server Error` - FinnHub API error

---

## News Endpoints

### 1. Get All News

**Endpoint:** `GET /api/news`

**Authentication:** Not Required (Public)

**Description:** Retrieve all latest news articles across all sectors.

**Request:**
```
GET /api/news?limit=50
```

**Query Parameters:**
- `limit` (integer, optional, default: 50) - Maximum number of articles to return

**Response: 200 OK**
```json
[
  {
    "sector": "Technology",
    "title": "Apple Releases New M4 Chip",
    "summary": "Apple announces the new M4 chip with improved performance and efficiency...",
    "url": "https://example.com/article1",
    "publishedAt": "2025-12-06T10:30:00Z"
  },
  {
    "sector": "FinTech",
    "title": "Major Banks Adopt Blockchain Technology",
    "summary": "Leading financial institutions announce blockchain integration initiatives...",
    "url": "https://example.com/article2",
    "publishedAt": "2025-12-06T09:15:00Z"
  },
  {
    "sector": "Other",
    "title": "Energy Sector Reports Record Profits",
    "summary": "Energy companies report strong quarterly earnings...",
    "url": "https://example.com/article3",
    "publishedAt": "2025-12-05T14:45:00Z"
  }
]
```

**Error Responses:**
- `400 Bad Request` - Invalid query parameters
- `500 Internal Server Error` - Server error

---

### 2. Get News by Sector

**Endpoint:** `GET /api/news/sector/{sector}`

**Authentication:** Not Required (Public)

**Description:** Retrieve news articles filtered by a specific sector.

**Request:**
```
GET /api/news/sector/Technology?limit=25
```

**Path Parameters:**
- `sector` (string, required) - Frontend sector name. Valid values:
  - `Technology`
  - `Semiconductors`
  - `FinTech`
  - `Consumer/Tech`
  - `Healthcare`
  - `Retail`

**Query Parameters:**
- `limit` (integer, optional, default: 50) - Maximum number of articles to return

**Response: 200 OK**
```json
[
  {
    "sector": "Technology",
    "title": "Apple Releases New M4 Chip",
    "summary": "Apple announces the new M4 chip with improved performance and efficiency...",
    "url": "https://example.com/article1",
    "publishedAt": "2025-12-06T10:30:00Z"
  },
  {
    "sector": "Technology",
    "title": "Microsoft Expands AI Services",
    "summary": "Microsoft launches new AI-powered cloud services for enterprises...",
    "url": "https://example.com/article2",
    "publishedAt": "2025-12-06T08:20:00Z"
  }
]
```

**Error Responses:**
- `400 Bad Request` - Invalid sector name
- `500 Internal Server Error` - Server error

---

### 3. Get News by Multiple Sectors

**Endpoint:** `GET /api/news/sectors`

**Authentication:** Not Required (Public)

**Description:** Retrieve news articles filtered by multiple sectors.

**Request:**
```
GET /api/news/sectors?sectors=Technology,FinTech,Healthcare&limit=100
```

**Query Parameters:**
- `sectors` (string, required) - Comma-separated list of frontend sector names. Valid values:
  - `Technology`
  - `Semiconductors`
  - `FinTech`
  - `Consumer/Tech`
  - `Healthcare`
  - `Retail`
- `limit` (integer, optional, default: 50) - Maximum number of articles to return

**Response: 200 OK**
```json
[
  {
    "sector": "Technology",
    "title": "Apple Releases New M4 Chip",
    "summary": "Apple announces the new M4 chip with improved performance and efficiency...",
    "url": "https://example.com/article1",
    "publishedAt": "2025-12-06T10:30:00Z"
  },
  {
    "sector": "FinTech",
    "title": "Major Banks Adopt Blockchain Technology",
    "summary": "Leading financial institutions announce blockchain integration initiatives...",
    "url": "https://example.com/article2",
    "publishedAt": "2025-12-06T09:15:00Z"
  },
  {
    "sector": "Healthcare",
    "title": "New Drug Receives FDA Approval",
    "summary": "Pharmaceutical company announces breakthrough drug approval...",
    "url": "https://example.com/article3",
    "publishedAt": "2025-12-05T16:30:00Z"
  }
]
```

**Error Responses:**
- `400 Bad Request` - Invalid sector names or missing required parameters
- `500 Internal Server Error` - Server error

---

## Sector Mapping Information

### Frontend Sectors vs. External Data

Your frontend defines **6 main sectors**. Below is how backend data maps into these sectors:

#### 1. Technology
**Frontend Sector:** `Technology`

**Mapped From (MarketAux industries):**
- `technology` (direct match)
- `communication services` (telecom/communication tech)

**Example News:** Apple M4 chip release, Microsoft AI services

---

#### 2. Semiconductors
**Frontend Sector:** `Semiconductors`

**Mapped From (MarketAux industries):**
- `technology` (specialty semiconductor industry)

**Note:** MarketAux doesn't have a dedicated "semiconductors" industry. Articles related to chip manufacturers (Intel, NVIDIA, AMD, etc.) will typically be classified as `technology` and mapped to this sector when context indicates semiconductor focus.

**Example News:** Intel process improvements, NVIDIA GPU releases

---

#### 3. FinTech
**Frontend Sector:** `FinTech`

**Mapped From (MarketAux industries):**
- `financial` (direct match)
- `financial services` (financial technology services)

**Example News:** Bank blockchain adoption, fintech startup funding, cryptocurrency developments

---

#### 4. Consumer/Tech
**Frontend Sector:** `Consumer/Tech`

**Mapped From (MarketAux industries):**
- `services` (consumer-facing services)
- `consumer cyclical` (consumer tech products/services)
- `consumer defensive` (essential consumer services)

**Example News:** E-commerce platforms, consumer app releases, digital services

---

#### 5. Healthcare
**Frontend Sector:** `Healthcare`

**Mapped From (MarketAux industries):**
- `healthcare` (direct match)

**Example News:** Drug approvals, hospital expansions, healthcare technology, biotech breakthroughs

---

#### 6. Retail
**Frontend Sector:** `Retail`

**Mapped From (MarketAux industries):**
- `consumer goods` (consumer products)
- `consumer cyclical` (cyclical retail)
- `consumer defensive` (defensive retail/necessities)

**Example News:** Store openings, retail earnings, consumer product launches

---

### Unmapped Industries → "Other" Sector

Articles from these MarketAux industries map to **"Other"** sector:
- `basic materials`
- `energy`
- `industrial goods`
- `industrials`
- `real estate`
- `utilities`

**Handling in Frontend:**
The backend automatically maps unmapped industries to "Other". The frontend should:
1. Display "Other" sector articles if requested
2. Be prepared to receive "Other" sector in news responses
3. Optionally filter them out if desired

---

### Reverse Mapping (Frontend → Backend API Calls)

When your frontend requests news by sector, the backend automatically converts to industry filters:

| Frontend Sector | Backend Industries Used |
|---|---|
| Technology | technology, communication services |
| Semiconductors | technology |
| FinTech | financial, financial services |
| Consumer/Tech | services, consumer_cyclical, consumer_defensive |
| Healthcare | healthcare |
| Retail | consumer_goods, consumer_cyclical, consumer_defensive |

**Example Frontend Implementation:**
```javascript
// Frontend requests news for Technology sector
fetchNewsBySector('Technology');

// Backend automatically queries MarketAux with:
// industries=technology,communication services
```

---

## Error Handling

### Error Response Format

All error responses follow this JSON structure:

```json
{
  "error": "Error message description",
  "status": 400,
  "timestamp": "2025-12-06T10:30:00Z"
}
```

**Note:** Some endpoints may return plain text or HTML for errors. Always check `Content-Type` header.

### Common HTTP Status Codes

| Code | Meaning | Typical Cause |
|---|---|---|
| 200 | OK | Successful request |
| 201 | Created | Successful resource creation |
| 204 | No Content | Successful deletion |
| 400 | Bad Request | Invalid parameters or malformed request |
| 401 | Unauthorized | Missing or invalid JWT token |
| 404 | Not Found | Resource does not exist |
| 500 | Internal Server Error | Server error (try again later) |
| 503 | Service Unavailable | External API (FinnHub/MarketAux) temporarily unavailable |

### Frontend Error Handling Strategy

```javascript
// Pseudo-code example
async function fetchNewsData(sector) {
  try {
    const response = await fetch(`/api/news/sector/${sector}`);

    if (!response.ok) {
      if (response.status === 400) {
        // Invalid sector name
        console.error('Invalid sector:', sector);
      } else if (response.status === 500) {
        // Server error - show user a retry message
        console.error('Server error, please try again later');
      }
      return [];
    }

    return await response.json();
  } catch (error) {
    // Network error
    console.error('Network error:', error);
    return [];
  }
}
```

### FinnHub/MarketAux API Failures

**Graceful Degradation:**
- Stock current price falls back to last known database value if FinnHub is unavailable
- News requests return empty array if MarketAux API is down
- Frontend should display cached/previous data or a "data unavailable" message

---

## Response Schemas

### Stock Response Schema

```typescript
interface StockDTO {
  stockId: string;           // UUID
  stockCode: string;         // e.g., "AAPL"
  companyName: string;       // e.g., "Apple Inc."
  stockValue: number;        // Decimal value, e.g., 195.50
}
```

### Stock Price Response Schema

```typescript
type StockPrice = number;  // Simple decimal value
```

### FinnHub Quote Response Schema

```typescript
interface FinnhubQuoteDTO {
  currentPrice: number;           // e.g., 195.75
  highPrice: number;              // e.g., 198.50
  lowPrice: number;               // e.g., 192.10
  openPrice: number;              // e.g., 194.25
  previousClosePrice: number;     // e.g., 194.80
  timestamp: number;              // Unix timestamp
}
```

### FinnHub Company Profile Response Schema

```typescript
interface FinnhubCompanyProfileDTO {
  ticker: string;                 // e.g., "AAPL"
  companyName: string;            // e.g., "Apple Inc."
  industry: string;               // e.g., "technology"
  marketCap: number;              // e.g., 3000000000000
  logo: string;                   // URL to logo
  country: string;                // e.g., "US"
  currency: string;               // e.g., "USD"
  phone: string;                  // e.g., "+1-408-996-1010"
  website: string;                // e.g., "https://www.apple.com"
  description: string;            // Company description
}
```

### News Article Response Schema

```typescript
interface NewsArticleDTO {
  sector: string;           // One of: Technology, Semiconductors, FinTech, Consumer/Tech, Healthcare, Retail, Other
  title: string;            // Article headline
  summary: string;          // Article description/summary
  url: string;              // Full URL to article
  publishedAt: string;      // ISO 8601 timestamp, e.g., "2025-12-06T10:30:00Z"
}
```

---

## Implementation Examples

### React Example: Fetching Stock Data

```javascript
import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080';

// Get current price for a stock
async function getStockPrice(stockId, jwtToken) {
  try {
    const response = await axios.get(
      `${API_BASE_URL}/api/stocks/${stockId}/price`,
      {
        headers: {
          'Authorization': `Bearer ${jwtToken}`,
          'Content-Type': 'application/json'
        }
      }
    );
    return response.data; // Returns: 195.75
  } catch (error) {
    console.error('Error fetching stock price:', error);
    return null;
  }
}

// Get real-time quote from FinnHub (no auth required)
async function getStockQuote(symbol) {
  try {
    const response = await axios.get(
      `${API_BASE_URL}/api/stocks/finnhub/quote/${symbol}`
    );
    return response.data;
    // Returns: { currentPrice: 195.75, highPrice: 198.50, ... }
  } catch (error) {
    console.error('Error fetching stock quote:', error);
    return null;
  }
}
```

### React Example: Fetching News Data

```javascript
// Get all news
async function getAllNews(limit = 50) {
  try {
    const response = await axios.get(
      `${API_BASE_URL}/api/news`,
      { params: { limit } }
    );
    return response.data; // Array of NewsArticleDTO
  } catch (error) {
    console.error('Error fetching news:', error);
    return [];
  }
}

// Get news by sector
async function getNewsBySector(sector, limit = 50) {
  try {
    const response = await axios.get(
      `${API_BASE_URL}/api/news/sector/${sector}`,
      { params: { limit } }
    );
    return response.data; // Array of NewsArticleDTO filtered by sector
  } catch (error) {
    console.error(`Error fetching news for sector ${sector}:`, error);
    return [];
  }
}

// Get news by multiple sectors
async function getNewsByMultipleSectors(sectors, limit = 50) {
  try {
    const response = await axios.get(
      `${API_BASE_URL}/api/news/sectors`,
      { params: { sectors: sectors.join(','), limit } }
    );
    return response.data; // Array of NewsArticleDTO from all selected sectors
  } catch (error) {
    console.error('Error fetching news:', error);
    return [];
  }
}
```

### React Native Example: Using fetch API

```javascript
// Get news with fetch (no axios dependency)
async function getNewsBySector(sector, limit = 50) {
  try {
    const url = new URL(`${API_BASE_URL}/api/news/sector/${sector}`);
    url.searchParams.append('limit', limit);

    const response = await fetch(url.toString(), {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json'
      }
    });

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    return await response.json();
  } catch (error) {
    console.error('Error fetching news:', error);
    return [];
  }
}
```

---

## Summary

### Quick Reference Table

| Resource | Endpoint | Method | Auth | Purpose |
|---|---|---|---|---|
| Stocks | `/api/stocks` | GET | ✅ | List all stocks |
| Stock | `/api/stocks/{id}` | GET | ✅ | Get single stock |
| Stock Price | `/api/stocks/{id}/price` | GET | ✅ | Get current price |
| Create Stock | `/api/stocks` | POST | ✅ | Add new stock |
| Update Stock | `/api/stocks/{id}` | PUT | ✅ | Update stock |
| Delete Stock | `/api/stocks/{id}` | DELETE | ✅ | Remove stock |
| Stock Quote | `/api/stocks/finnhub/quote/{symbol}` | GET | ❌ | Real-time price data |
| Stock Profile | `/api/stocks/finnhub/profile/{symbol}` | GET | ❌ | Company info |
| All News | `/api/news` | GET | ❌ | Get all news articles |
| News by Sector | `/api/news/sector/{sector}` | GET | ❌ | News for one sector |
| News by Sectors | `/api/news/sectors?sectors=...` | GET | ❌ | News for multiple sectors |

---

## Notes for Frontend Team

1. **JWT Token Management:** Store the JWT token securely after login. Include it in all authenticated requests.

2. **CORS:** The backend allows cross-origin requests, so direct API calls from your React/React Native app are permitted.

3. **Error Handling:** Always handle potential null/empty responses from external APIs (FinnHub, MarketAux).

4. **Rate Limiting:** Be mindful of external API rate limits. Consider caching responses where appropriate.

5. **Sector Names:** When making requests, use exact casing: `Technology`, `FinTech`, etc. (not `technology`, `fintech`).

6. **Timestamps:** All `publishedAt` timestamps in news articles are in ISO 8601 format (UTC). Format them for your UI as needed.

7. **Pagination:** Currently, the API uses a `limit` parameter. Implement pagination on the frontend by fetching data in batches if needed.

