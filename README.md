# 🛒 Online Shopping Web Application

A fully functional e-commerce web application built using Java Full Stack technologies including Spring Boot, Spring Security, MySQL, JSP, Bootstrap, jQuery, and AngularJS. The application allows users to browse products, manage their shopping cart, and place orders with SMS confirmation.

---

## 🖥️ Tech Stack

| Layer | Technology |
|---|---|
| Backend | Java, Spring Boot, Spring MVC, Spring Security |
| Database | MySQL, Spring Data JPA, Hibernate |
| Frontend | JSP, Bootstrap 3, jQuery, AngularJS |
| Plugins | DataTables, Bootbox.js, jQuery Validate |
| SMS Service | Fast2SMS API |
| Build Tool | Maven |
| Server | Embedded Tomcat |

---

## ✨ Features

### User Features
- User Registration and Login with Spring Security
- Browse All Products with dynamic DataTable and Search
- Filter Products by Category
- View Single Product Details
- Add Products to Shopping Cart
- Update Cart Item Quantity
- Remove Items from Cart
- Checkout with Order Summary and Customer Details
- SMS Order Confirmation via Fast2SMS API

### Admin Features
- Add and Edit Products with Image Upload
- Activate and Deactivate Products
- Manage Product Categories
- View All Products in Admin Panel

---

## ⚙️ Prerequisites

Make sure you have the following installed:

- Java JDK 17 or above
- Maven 3.6 or above
- MySQL 8.0 or above
- Eclipse IDE or Spring Tool Suite (STS)
- Git

---

## 🗄️ Database Setup

**Step 1 — Create the database:**
```sql
CREATE DATABASE online_shopping_db;
```

**Step 2 — Insert categories:**
```sql
INSERT INTO category (name, description, image_url, is_active)
VALUES ('Electronics', 'Electronic gadgets and devices', '', true);

INSERT INTO category (name, description, image_url, is_active)
VALUES ('Clothing', 'Fashion and apparel', '', true);

INSERT INTO category (name, description, image_url, is_active)
VALUES ('Books', 'All kinds of books', '', true);
```

**Step 3 — Insert admin user (password: admin123):**
```sql
INSERT INTO user_detail 
(first_name, last_name, email, contact_number, role, password, enabled)
VALUES 
('Admin', 'User', 'admin@shop.com', '9999999999', 'ADMIN',
'$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6pbmG', true);
```

**Step 4 — Insert normal user (password: admin123):**
```sql
INSERT INTO user_detail 
(first_name, last_name, email, contact_number, role, password, enabled)
VALUES 
('Test', 'User', 'user@shop.com', '9876543210', 'USER',
'$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6pbmG', true);
```

**Step 5 — Insert cart for users:**
```sql
INSERT INTO cart (grand_total, cart_lines, user_id)
VALUES (0.0, 0, 1);

INSERT INTO cart (grand_total, cart_lines, user_id)
VALUES (0.0, 0, 2);
```

---

## 🔧 Configuration

Update `src/main/resources/application.properties`:

```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/online_shopping_db
spring.datasource.username=root
spring.datasource.password=your_mysql_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Security Queries
spring.queries.users-query=select email, password, enabled from user_detail where email=?
spring.queries.roles-query=select email, role from user_detail where email=?

# Fast2SMS Configuration
fast2sms.api.key=YOUR_FAST2SMS_API_KEY
fast2sms.to.number=YOUR_10_DIGIT_MOBILE_NUMBER

# Server Port
server.port=8080
```

---

## 🚀 How to Run

**Step 1 — Clone the repository:**
```bash
git clone https://github.com/your-username/online-shopping.git
```

**Step 2 — Navigate to project directory:**
```bash
cd online-shopping
```

**Step 3 — Build the project:**
```bash
mvn clean install
```

**Step 4 — Run the application:**
```bash
mvn spring-boot:run
```

**Step 5 — Open in browser:**
http://localhost:8081

---

## 🔐 Login Credentials

| Role | Email | Password |
|---|---|---|
| Admin | admin@shop.com | admin123 |
| User | user@shop.com | admin123 |

---

## 📱 Application Pages

| URL | Description |
|---|---|
| `/home` | Home page with most viewed and purchased products |
| `/show/all/products` | All products listing with DataTable |
| `/show/category/{id}/products` | Products filtered by category |
| `/show/{id}/product` | Single product detail page |
| `/cart/show` | Shopping cart page |
| `/cart/validate` | Checkout page |
| `/cart/placeorder` | Place order with SMS confirmation |
| `/manage/products` | Admin product management |
| `/about` | About Us page |
| `/contact` | Contact Us page |
| `/login` | Login page |

---

## 🛠️ Built With

- [Spring Boot](https://spring.io/projects/spring-boot) - Backend Framework
- [Spring Security](https://spring.io/projects/spring-security) - Authentication and Authorization
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa) - Database ORM
- [MySQL](https://www.mysql.com/) - Relational Database
- [Bootstrap 3](https://getbootstrap.com/docs/3.4/) - Frontend CSS Framework
- [jQuery](https://jquery.com/) - JavaScript Library
- [AngularJS](https://angularjs.org/) - Frontend MVC Framework
- [DataTables](https://datatables.net/) - Table Plugin
- [Fast2SMS](https://www.fast2sms.com/) - SMS Service

---

## 👩‍💻 Developer

**Anjushree J**
- Internship Project at Palle Consultation Services Pvt Ltd, Bangalore
- Dept of CSE, VKIT

---

## 📄 License

This project is developed for internship and educational purposes.
