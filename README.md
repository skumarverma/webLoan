# Castle Financial Services - Loan Management Website

A responsive financial services website built with Spring Boot 4.0.2 that allows users to view loan offers, check eligibility criteria, and submit loan applications.

## 🎯 Project Features

### Home Page (Landing Page)
- **Header Section**: Logo, company name, contact information
- **Hero Section**: 
  - Personal Loan for Salaried Employees
  - Interest Rate display (10.99%)
  - Flexible Tenure information
  - Eligibility Criteria
  - Required Documents
  - Prominent "Apply Today" button
- **Features Section**: Why choose Castle benefits
- **Footer**: Company information and quick links

### Apply Page
- **Navigation Bar**: 
  - Personal Loan
  - Home Loan
  - Car Loan
  - Business Loan
  - Loan Against Property
  - Mudra Loan
  - Overdraft Loan
- **Comprehensive Application Form**:
  - Personal Information
  - Loan Details
  - Address Information
  - Terms and Conditions
- **Form Validation**: Client-side validation with real-time feedback
- **Success/Error Messages**: User-friendly feedback

## 🛠️ Technology Stack

- **Backend**: Spring Boot 4.0.2
- **Java**: Java 17
- **Database**: H2 (in-memory for development)
- **ORM**: Spring Data JPA with Hibernate
- **Template Engine**: Thymeleaf
- **Security**: Spring Security (basic configuration)
- **Frontend**: HTML5, CSS3, JavaScript
- **Build Tool**: Maven

## 📁 Project Structure

```
src/
├── main/
│   ├── java/com/sachin/webLoan/
│   │   ├── controller/
│   │   │   └── HomeController.java
│   │   ├── entity/
│   │   │   ├── LoanApplication.java
│   │   │   └── User.java
│   │   ├── repository/
│   │   │   ├── LoanApplicationRepository.java
│   │   │   └── UserRepository.java
│   │   └── WebLoanApplication.java
│   └── resources/
│       ├── static/
│       │   ├── css/
│       │   │   └── style.css
│       │   └── js/
│       │       └── main.js
│       ├── templates/
│       │   ├── index.html
│       │   └── apply.html
│       └── application.properties
└── test/
    └── java/com/sachin/webLoan/
        └── LoanApplicationTest.java
```

## 🚀 Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6+

### Running the Application

1. **Clone the repository** (if applicable)
2. **Navigate to project directory**:
   ```bash
   cd webLoan
   ```
3. **Run the application**:
   ```bash
   ./mvnw spring-boot:run
   ```
4. **Access the application**:
   - Open browser and go to: `http://localhost:8080`
   - H2 Database Console: `http://localhost:8080/h2-console`
     - JDBC URL: `jdbc:h2:mem:cashtechdb`
     - Username: `sa`
     - Password: `password`

### Database Configuration

The application uses H2 in-memory database for development. For production, you can configure MySQL by:
1. Uncomment MySQL dependencies in `pom.xml`
2. Update `application.properties` with MySQL configuration
3. Create the database: `cashtech_loan_db`

## 🧪 Testing

Run tests with:
```bash
./mvnw test
```

## 📱 Responsive Design

The website is fully responsive and works on:
- Desktop computers
- Tablets
- Mobile devices

Key responsive features:
- Flexible grid layouts
- Mobile-friendly navigation
- Touch-optimized form elements
- Adaptive font sizing

## 🎨 Features Implemented

### Home Page Features
- [x] Professional header with contact information
- [x] Eye-catching hero section with loan details
- [x] Eligibility criteria display
- [x] Document requirements listing
- [x] "Apply Today" call-to-action button
- [x] Company benefits/features section
- [x] Professional footer

### Apply Page Features
- [x] Navigation bar with loan type options
- [x] Comprehensive application form
- [x] Form validation (client-side)
- [x] Success/error message handling
- [x] Data persistence to database
- [x] Responsive design

### Technical Features
- [x] Spring Boot REST controller
- [x] JPA entity mapping
- [x] Database repository patterns
- [x] Thymeleaf template integration
- [x] CSS styling with Flexbox/Grid
- [x] JavaScript form handling
- [x] Automated testing

## 🔄 How It Works

1. **User visits homepage** (`/`) - sees loan information and benefits
2. **Clicks "Apply Today"** - navigates to application page (`/apply`)
3. **Fills application form** - provides personal and loan details
4. **Submits form** - data is validated and saved to database
5. **Receives confirmation** - success message with application ID

## 🛡️ Security Notes

⚠️ **Development Only Configuration**
- Generated security passwords are for development use
- Basic authentication is enabled by default
- Update security configuration for production deployment

## 📊 Database Schema

### Loan Applications Table
- `id` (Primary Key)
- `full_name` - Applicant's full name
- `email` - Email address
- `phone` - Phone number
- `loan_type` - Type of loan requested
- `loan_amount` - Requested loan amount
- `employment_status` - Current employment status
- `monthly_income` - Monthly income
- `company_name` - Company name (optional)
- `address` - Full address
- `city`, `state`, `pincode` - Location details
- `application_date` - Timestamp
- `status` - Application status (default: PENDING)

### Users Table
- `id` (Primary Key)
- `username` - Unique username
- `email` - Unique email
- `password` - Hashed password
- `full_name` - User's full name
- `phone` - Phone number
- `role` - User role (default: USER)
- `created_date` - Account creation timestamp
- `is_active` - Account status

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## 📄 License

This project is for educational purposes. All rights reserved.

## 🆘 Support

For support, please contact:
- Email: info@Castle.com
- Phone: +1 (555) 123-4567

---

**Castle Financial Services** - Your trusted partner for financial solutions! 💰