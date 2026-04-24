# 🍲 FoodBridge: Connecting Kitchens, Communities, and Compassion

FoodBridge is a comprehensive digital platform designed to bridge the gap between talented homemakers, hungry customers, and NGOs dedicated to fighting food waste. Our mission is to empower local culinary talent while ensuring that surplus food reaches those who need it most.

---

## 🚀 Key Features

### 👥 Role-Based Ecosystem
- **Admin**: Oversees the platform, verifies user profiles (NGOs/Homemakers), and manages system integrity.
- **Homemaker**: Lists home-cooked meals for sale or donation, manages availability, and tracks earnings/contributions.
- **Customer**: Browses local listings, places orders, and supports local culinary talent.
- **NGO**: Receives food donations from homemakers to support community welfare projects.

### 🍱 Core Functionality
- **Dynamic Food Listings**: Real-time availability with timing slots, quantity tracking, and dietary labels (Veg/Non-Veg).
- **Seamless Ordering**: Integrated cart system and order tracking.
- **Donation Workflow**: Dedicated pipeline for surplus food donations to verified NGOs.
- **Profile Verification**: Secure document upload and admin approval workflow for sensitive roles.

---

## 🛠️ Technology Stack

| Layer | Technology |
| :--- | :--- |
| **Frontend** | Angular 19+, RXJS, Vanilla CSS |
| **Backend** | Spring Boot 4.0.5, Java 17 |
| **Database** | H2 In-Memory Database (with persistent profiling options) |
| **Security** | JWT (JSON Web Tokens), Spring Security |
| **Utilities** | Lombok, JPA, Hibernate, Maven |

---

## 📂 Project Structure

```text
FoodBridge/
├── foodbridge-backend/      # Spring Boot REST API
│   ├── src/                 # Java source code & resources
│   └── pom.xml              # Maven dependencies
├── foodbridge-frontend/     # Angular Single Page Application
│   ├── src/                 # TypeScript components & styling
│   └── package.json         # Node.js dependencies
├── uploads/                 # Storage for ID proofs and food images
└── implementation_plan.md   # Project roadmap and architecture
```

---

## ⚙️ Getting Started

### Prerequisites
- **JDK 17** or higher
- **Node.js** (v18+) and **npm**
- **Angular CLI** (`npm install -g @angular/cli`)
- **Maven** (optional, wrapper included)

### Backend Setup
1. Navigate to the backend directory:
   ```bash
   cd foodbridge-backend
   ```
2. Run the application using the Maven wrapper:
   ```bash
   ./mvnw spring-boot:run
   ```
3. The API will be available at `http://localhost:8080`.
4. Access H2 Console: `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:foodbridgedb`)

### Frontend Setup
1. Navigate to the frontend directory:
   ```bash
   cd foodbridge-frontend
   ```
2. Install dependencies:
   ```bash
   npm install
   ```
3. Start the development server:
   ```bash
   npm start
   ```
4. Open your browser at `http://localhost:4200`.

---

## 🔒 Security & Authentication
The platform uses **JWT-based authentication**. On successful login, the server returns a token which the frontend must include in the `Authorization: Bearer <token>` header for all subsequent protected API requests.

---

## 📸 Screenshots & Assets
*Placeholder for UI Walkthroughs*
> [!TIP]
> Check the `uploads/` directory for sample ID proofs and culinary imagery used during development.

---

## 📄 License
This project is for educational and community development purposes.

---
*Built with ❤️ for a Zero-Waste Future.*
