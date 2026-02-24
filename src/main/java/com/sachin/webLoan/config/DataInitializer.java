package com.sachin.webLoan.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.sachin.webLoan.entity.LoanApplication;
import com.sachin.webLoan.entity.User;
import com.sachin.webLoan.repository.LoanApplicationRepository;
import com.sachin.webLoan.repository.UserRepository;
import com.sachin.webLoan.util.SimplePasswordEncoder;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private SimplePasswordEncoder passwordEncoder;
    
    @Autowired
    private LoanApplicationRepository loanApplicationRepository;
    
    @Override
    public void run(String... args) throws Exception {
        // Create default admin user if it doesn't exist
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@castle.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setFullName("Administrator");
            admin.setRole("ADMIN");
            admin.setIsActive(true);
            userRepository.save(admin);
            System.out.println("Default admin user created: admin/admin123");
        }
        
        // Create default customer user if it doesn't exist
        if (!userRepository.existsByUsername("customer")) {
            User customer = new User();
            customer.setUsername("customer");
            customer.setEmail("customer@castle.com");
            customer.setPassword(passwordEncoder.encode("customer123"));
            customer.setFullName("Customer User");
            customer.setRole("USER");
            customer.setIsActive(true);
            userRepository.save(customer);
            System.out.println("Default customer user created: customer/customer123");
        }
        
        // Create sample loan applications if none exist
        if (loanApplicationRepository.count() == 0) {
            // Sample Personal Loan Application
            LoanApplication app1 = new LoanApplication();
            app1.setFullName("John Smith");
            app1.setEmail("john.smith@email.com");
            app1.setPhone("9876543210");
            app1.setLoanType("Personal Loan");
            app1.setLoanAmount(50000.0);
            app1.setEmploymentStatus("Employed");
            app1.setMonthlyIncome(45000.0);
            app1.setCompanyName("Tech Solutions Inc.");
            app1.setAddress("123 Main Street, Apartment 4B");
            app1.setCity("Mumbai");
            app1.setState("Maharashtra");
            app1.setPincode("400001");
            app1.setStatus("PENDING");
            // Let @PrePersist handle the applicationDate
            loanApplicationRepository.save(app1);
            
            // Sample Home Loan Application
            LoanApplication app2 = new LoanApplication();
            app2.setFullName("Priya Sharma");
            app2.setEmail("priya.sharma@email.com");
            app2.setPhone("9876543211");
            app2.setLoanType("Home Loan");
            app2.setLoanAmount(2500000.0);
            app2.setEmploymentStatus("Self Employed");
            app2.setMonthlyIncome(80000.0);
            app2.setCompanyName("Sharma Enterprises");
            app2.setAddress("456 Park Avenue, Flat 12C");
            app2.setCity("Delhi");
            app2.setState("Delhi");
            app2.setPincode("110001");
            app2.setStatus("APPROVED");
            // Let @PrePersist handle the applicationDate
            loanApplicationRepository.save(app2);
            
            // Sample Car Loan Application
            LoanApplication app3 = new LoanApplication();
            app3.setFullName("Raj Kumar");
            app3.setEmail("raj.kumar@email.com");
            app3.setPhone("9876543212");
            app3.setLoanType("Car Loan");
            app3.setLoanAmount(800000.0);
            app3.setEmploymentStatus("Employed");
            app3.setMonthlyIncome(60000.0);
            app3.setCompanyName("Global Motors Ltd.");
            app3.setAddress("789 Business Park, Suite 301");
            app3.setCity("Bangalore");
            app3.setState("Karnataka");
            app3.setPincode("560001");
            app3.setStatus("REJECTED");
            // Let @PrePersist handle the applicationDate
            loanApplicationRepository.save(app3);
            
            System.out.println("Sample loan applications created for testing");
        }
    }
}