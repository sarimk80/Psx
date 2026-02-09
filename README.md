# 📈 PSX – Pakistan Stock Exchange App

A modern Android application providing **real-time PSX market data**, **company insights**, **portfolio tracking**, and a clean UI for Pakistani stock market investors. Stay updated with market movements, price changes, and live charts — all in one beautifully designed app.

---

## 🚀 Features

### 📊 **Market Overview**  
- **Live PSX Indices**: KSE-100, KSE-30, and All Share Index  
- **Real-time Market Data**: Daily highs, lows, and complete market summary  
- **Price Updates**: Live ticker with percentage changes  
- **Sector-wise Performance**: Detailed sector distribution charts

### 🔍 **Company Intelligence**  
- **Comprehensive Company Data**: EPS, dividends, and financial ratios  
- **Interactive Price Charts**: Historical and intraday charts  
- **Corporate Actions**: AGM dates, bonus issues, and rights shares  
- **Fundamental Analysis**: P/E ratios, market capitalization, and volume analysis

### 📅 **Portfolio & Watchlist**  
- **Personal Portfolio Management**: Track your investments with real-time P&L  
- **Smart Watchlist**: Add stocks for quick monitoring  
- **Price Alerts**: Set custom triggers for price movements  
- **Sector Allocation**: Visual breakdown of your portfolio by sector

### 📱 **User Experience**  
- **Dark/Light Theme**: Full Material 3 theming support  
- **Offline Access**: View cached data when offline  
- **Push Notifications**: Breaking news and market alerts  
- **Intuitive Navigation**: Easy access to all market segments

---

## 📱 Screenshots

### **Market & Indices**
| Home Screen | Market Indices | Sector Distribution |
|-------------|----------------|---------------------|
| <img src="https://github.com/user-attachments/assets/9abb5444-b3b0-4921-94ff-2c87c16b0bb7" width="250" alt="Detailed View 4"/>| <img src="https://github.com/user-attachments/assets/08e376f8-03a0-413d-83bf-d785d61450ba" width="250" alt="Market Indices"/> | <img src="https://github.com/user-attachments/assets/fcddca11-3417-4bae-b5ef-3385a4b7eb83" width="250" alt="Sector Distribution"/> |

### **Stock Details & Analysis**
| Stock Details | Price Charts | Company Financials |
|---------------|--------------|-------------------|
| <img src="https://github.com/user-attachments/assets/942a936a-f48c-4266-8e0a-c59eed01dcdb" width="250" alt="Stock Details"/> | <img src="https://github.com/user-attachments/assets/d5361f86-f62a-4959-b112-9861df628cb1" width="250" alt="Price Charts"/> | <img src="https://github.com/user-attachments/assets/6ca4a30e-3266-433c-8bcb-c15d3f386832" width="250" alt="Company Financials"/> |

### **Portfolio & Watchlist**
| Portfolio Overview | Watchlist | News & Alerts |
|--------------------|-----------|---------------|
| <img src="https://github.com/user-attachments/assets/9a6dc8c0-3b68-4efe-baaa-894c54a6596a" width="250" alt="Portfolio Overview"/> | <img src="https://github.com/user-attachments/assets/4418524a-c727-4835-ad7f-f3112ec3fac6" width="250" alt="Watchlist"/> | <img src="https://github.com/user-attachments/assets/118fc31a-9b46-423d-823e-bb89a1d025a2" width="250" alt="News & Alerts"/> |

### **Detailed Views**
<div align="center">
  <img src="https://github.com/user-attachments/assets/082564e8-35ed-4cb8-acfb-a0509905fdd1" width="200" alt="Detailed View 1"/>
  <img src="https://github.com/user-attachments/assets/8ee14fe8-c4d2-4e7c-8bde-25a8b9cd66f2" width="200" alt="Detailed View 2"/>
  <img src="https://github.com/user-attachments/assets/978bac76-1fb3-41d2-8bb8-e078462509e1" width="200" alt="Detailed View 3"/>
</div>

<div align="center">
  <img src="https://github.com/user-attachments/assets/d88af2e8-d532-4560-931e-bb276a743b37" width="200" alt="Detailed View 5"/>
  <img src="https://github.com/user-attachments/assets/e621d9f8-5996-4745-9428-a877c4f122e7" width="200" alt="Detailed View 6"/>
  <img src="https://github.com/user-attachments/assets/4133951d-9bd4-4d7b-b338-36ff1b129580" width="200" alt="Detailed View 7"/>
  <img src="https://github.com/user-attachments/assets/c9e4ef79-07de-49ed-98a7-0467a3a15a67" width="200" alt="Detailed View 8"/>
</div>

<div align="center">
  <img src="https://github.com/user-attachments/assets/49ba5a86-e045-4be0-a63d-bef4c7d1da07" width="200" alt="Detailed View 9"/>
  <img src="https://github.com/user-attachments/assets/3f4e2f35-8551-4b96-9c17-408c3888fde6" width="200" alt="Detailed View 10"/>
  <img src="https://github.com/user-attachments/assets/39eeeecf-1477-41f1-a46d-8e92cc452bbc" width="200" alt="Detailed View 11"/>
  <img src="https://github.com/user-attachments/assets/9021c8f0-6700-4f08-8732-3ac729a13dd1" width="200" alt="Detailed View 12"/>
</div>

---
## 🏗️ Architecture

This project follows **Clean Architecture + MVVM** to ensure separation of concerns and testability.



**Key Benefits:**
- Easier maintenance  
- Scalable codebase  
- Test-friendly structure  
- Industry-aligned patterns  

---

## 🛠️ Tech Stack

**Language:** Kotlin  
**Architecture:** MVVM + Clean Architecture  

### Android
- Jetpack Compose  
- Material 3  
- Navigation Component  

### Async & State
- Coroutines  
- Flow / StateFlow  

### Networking
- Retrofit  
- REST APIs  

### Local Storage
- Room Database  

### Dependency Injection
- Hilt  

---

## 📦 Getting Started

### Prerequisites
- Android Studio (latest recommended)
- Kotlin support
- Minimum SDK: *(add yours here)*

---

### Installation

```bash
# Clone the repository
git clone https://github.com/your-repo/psx-app.git

# Open in Android Studio
# Sync Gradle
# Run the app


## 📦 Installation & Setup

### **Prerequisites**
- Android Studio Hedgehog or later
- JDK 17 or higher
- Android SDK 34 (Android 14)
- Git

### **Clone & Build**
```bash
# Clone the repository
git clone https://github.com/your-username/psx-app.git

# Navigate to project directory
cd psx-app

# Open in Android Studio
# OR build from command line
./gradlew build

# Run on connected device
./gradlew installDebug
