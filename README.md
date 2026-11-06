# PC Configurations — Custom PC Builder E-Commerce Platform

A full-stack, responsive custom PC builder and hardware e-commerce web platform engineered with **Java Spring Boot**, **Hibernate / Spring Data JPA**, **MySQL**, and modern **Vanilla HTML5/CSS3/JavaScript (ES6 Modules)** with dark gaming aesthetics.

---

## 🚀 Key Features

### 1. Interactive Custom PC Builder
- **5-Slot Hardware Configuration:** Processor (CPU), Graphics Card (GPU), Memory (RAM), Storage (SSD/HDD), and Chassis (Cabinet).
- **Real-Time Compatibility Engine:** Automatically verifies electrical TDP requirements, component physical clearances, and bottleneck balances.
- **Dynamic Wattage & PSU Calculator:** Computes total system draw and recommends optimal Power Supply (PSU) capacity with headroom.
- **Undo/Redo History:** Stack-based state restoration for effortless build iteration.
- **One-Click Cart Transfer:** Moves the entire custom-built configuration directly into the user's shopping cart.
- **Saved Configurations:** Save named PC builds with notes to user profiles via REST API.

### 2. Component Catalog & E-Commerce
- **Catalog Browsing & Search:** Instant text search across title, brand, and specifications with category filtering.
- **Sorting Options:** Featured, Price (Low to High), Price (High to Low), and Name (A to Z).
- **Component Details Modal:** In-depth hardware specifications, brand certifications, pricing in INR (₹), and live stock availability badges.
- **Shopping Cart System:** Persistent user cart, itemized quantity controls (+/-), subtotal calculations, promo code validation, and 18% GST calculation.
- **Multi-Gateway Checkout:**
  - **UPI:** Instant collect requests (Google Pay, PhonePe, Paytm, BHIM)
  - **Cards:** Credit / Debit card simulation (Visa, MasterCard, RuPay)
  - **Net Banking:** Major banks support
  - **Cash on Delivery:** Courier handover option
- **Order Tracking & Invoices:** Visual milestone timeline (*Order Received → Verified → Bench Testing → Dispatched → Delivered*) and printable invoice generation.

### 3. Integrated Java Data Structures & Algorithms
- **MergeSort (`O(n log n)`):** Backend recursive divide-and-conquer algorithm for sorting catalog inventory by price and performance.
- **BinarySearch (`O(log n)`):** Fast logarithmic search algorithm for locating products by key within sorted hardware arrays.
- **Custom SinglyLinkedList:** Custom node-based pointer chain data structure implemented from scratch for managing hardware parts.
- **Generic Stack:** Custom LIFO data structure backing user build action undo/redo states.
- **Greedy PC Build Assistant (`GreedyBuildService` — Phase 4):**
  - **Strategy & Selection Rule:** Greedily selects the lowest-priced eligible in-stock component across all 5 mandatory hardware categories (`CPU`, `GPU`, `RAM`, `STORAGE`, `CABINET`), strictly bounding total expenditures to the user's budget.
  - **Time Complexity:** $O(N \log N)$ where $N$ is total inventory size (sorting per category via `ProductMergeSort`).
  - **Space Complexity:** $O(N)$ auxiliary sorting and selection storage.
  - **Limitations:** Focuses on minimum-cost budget satisfaction rather than multi-objective Pareto performance optimization across hardware bottlenecks.
- **Interactive DSA Playground:** Live benchmark dashboard executing algorithms directly against the backend REST endpoint (`/api/algorithms/demo`).

---

## 🛠️ Technology Stack

| Layer | Technologies |
|---|---|
| **Frontend** | HTML5, CSS3 (Custom Design System), Vanilla JavaScript (ES6 Modules), Bootstrap Icons |
| **Backend** | Java 17, Spring Boot 3.x, Spring Web MVC, Spring Data JPA, Hibernate, Spring Security, BCrypt, JJWT |
| **Database** | MySQL 8.x / MariaDB |
| **Build & Tooling** | Apache Maven 3.9+, Live Server / Static HTTP server |

---

## 📂 Project Structure

```text
PC Config/
├── backend/
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/pcconfigurations/backend/
│       │   ├── BackendApplication.java
│       │   ├── algorithms/               # LinkedList, Stack, BinarySearch, MergeSort
│       │   ├── config/                   # SecurityConfig, CorsConfig, DataLoader
│       │   ├── controller/               # REST API Controllers (Auth, Product, Cart, Build, Order, etc.)
│       │   ├── dto/                      # Data Transfer Objects
│       │   ├── entity/                   # JPA Entities (User, Product, Cart, Order, PcBuild, Payment)
│       │   ├── repository/               # Spring Data JPA Repositories
│       │   ├── security/                 # JWT AuthFilter, JwtUtils, UserDetails
│       │   └── service/                  # Business Logic Layer
│       └── resources/
│           └── application.properties    # Database credentials, JPA, JWT secrets
└── frontend/
    ├── index.html                        # Homepage with Hero, Category cards & Featured items
    ├── css/
    │   └── style.css                     # Comprehensive dark gaming design system
    ├── js/
    │   ├── api.js                        # Centralized fetch client with JWT interceptor
    │   ├── auth.js                       # User session & localStorage state manager
    │   ├── navbar.js                     # Global auth-aware header navigation & cart badge sync
    │   ├── toast.js                      # Toast notification alert system
    │   └── utils.js                      # Currency (₹), date formatters, and stock badges
    └── pages/
        ├── products.html                 # Components catalog with filters & details modal
        ├── builder.html                  # Interactive 5-slot custom PC builder
        ├── cart.html                     # Shopping cart with coupon code support
        ├── checkout.html                 # Shipping address & multi-gateway payment
        ├── orders.html                   # Orders history & live progress tracking timeline
        ├── login.html                    # User sign-in with quick demo credentials
        ├── register.html                 # User registration
        └── dashboard.html                # User dashboard & DSA benchmark playground
```

---

## ⚙️ Setup & Execution Guide

### Prerequisites
1. **Java JDK 17+**
2. **Apache Maven 3.9+**
3. **MySQL Server 8.0+** running locally on port `3306`

---

### Step 1: Database Setup
Open MySQL command line or MySQL Workbench:
```sql
CREATE DATABASE pc_configurations;
```

Set these environment variables before starting the backend. The application properties file contains no credentials:
```properties
DB_URL=jdbc:mysql://localhost:3306/pc_configurations?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
DB_USERNAME=root
DB_PASSWORD=your-local-mysql-password
JWT_SECRET=replace-with-a-long-random-secret
```
`DB_PASSWORD` may be empty for a local MySQL setup without a password. Set a unique, strong `JWT_SECRET` for each environment.

---

### Step 2: Start Spring Boot Backend
In terminal:
```bash
cd backend
mvn spring-boot:run
```
The backend starts at `http://localhost:8080`.
Upon the first boot, `DataLoader.java` automatically seeds:
- 15+ premier PC components (Intel/AMD CPUs, RTX 40-series GPUs, DDR5 RAM, Gen4 NVMe SSDs, Cabinets)
- Default Administrator Account:
- **Email:** `admin@pcconfigurations.com`
- **Password:** `admin123` (demo account only; change or remove it before deploying)

---

### Step 3: Run the Frontend
You can open `frontend/index.html` directly in any modern browser, or serve it using Python or Node http-server:

**Option A: Using Python**
```bash
cd frontend
python -m http.server 3000
```
Then navigate to: `http://localhost:3000`

**Option B: Using Node `npx serve`**
```bash
cd frontend
npx -y serve -l 3000
```

---

## 📡 REST API Reference

### Authentication (`/api/auth`)
| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/auth/register` | Register new user account |
| `POST` | `/api/auth/login` | Authenticate user & receive JWT token |
| `GET`  | `/api/auth/me` | Fetch authenticated user profile |

### Components & Products (`/api/products`)
| Method | Endpoint | Description |
|---|---|---|
| `GET`  | `/api/products` | Retrieve all products (supports category, search, pagination) |
| `GET`  | `/api/products/{id}` | Retrieve specific product by ID |
| `GET`  | `/api/products/search?q={query}` | Search inventory |

### Custom PC Builds (`/api/builds`)
| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/builds/greedy` | Calculate greedy PC build within target budget `{ "budget": 150000 }` (Phase 4) |
| `POST` | `/api/builds` | Save a new custom PC build (name, notes, componentIds) |
| `GET`  | `/api/builds` | Retrieve all saved builds for logged-in user |
| `GET`  | `/api/builds/{id}` | Retrieve specific build details |
| `DELETE` | `/api/builds/{id}` | Delete saved build |

### Shopping Cart (`/api/cart`)
| Method | Endpoint | Description |
|---|---|---|
| `GET`  | `/api/cart` | Get active user cart and subtotals |
| `POST` | `/api/cart/add` | Add product to cart `{ "productId": 1, "quantity": 1 }` |
| `PUT`  | `/api/cart/{itemId}` | Update quantity of cart item |
| `DELETE` | `/api/cart/{itemId}` | Remove item from cart |
| `DELETE` | `/api/cart/clear` | Clear entire cart |

### Orders & Checkout (`/api/orders`)
| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/orders` | Create order from cart with `{ "paymentMethod": "UPI" }` |
| `GET`  | `/api/orders` | Retrieve purchase history of user |
| `GET`  | `/api/orders/{id}` | Retrieve single order with status & timeline |

### Algorithms & Telemetry (`/api/algorithms`)
| Method | Endpoint | Description |
|---|---|---|
| `GET`  | `/api/algorithms/demo` | Executes MergeSort, BinarySearch, LinkedList, and Undo Stack benchmarks |
| `GET`  | `/api/dashboard` | Aggregated user stats (Total Spent, Total Orders, Saved Builds) |

---

## 🔒 Security
- Passwords hashed using **BCrypt** (12 salt rounds).
- Stateless **JWT (JSON Web Token)** Bearer token authentication on protected endpoints.
- Configured CORS policies for smooth frontend-backend integration.

---

## 👨‍💻 Developer Notes
- Frontend adheres strictly to modern **ES6 Modules** without bloated bundle overhead.
- All pricing formatted in Indian Rupees (**₹**) with standard Indian numbering groupings (`en-IN`).
