package com.sachin.webLoan;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import com.sachin.webLoan.entity.LoanApplication;
import com.sachin.webLoan.repository.LoanApplicationRepository;

@SpringBootTest
@TestPropertySource(locations = "classpath:application.properties")
public class LoanApplicationTest {
    
    @Autowired
    private LoanApplicationRepository loanApplicationRepository;
    
    @Test
    public void testSaveAndRetrieveLoanApplication() {
        // Create a test loan application
        LoanApplication loanApp = new LoanApplication();
        loanApp.setFullName("John Doe");
        loanApp.setEmail("john.doe@example.com");
        loanApp.setPhone("1234567890");
        loanApp.setLoanType("Personal Loan");
        loanApp.setLoanAmount(50000.0);
        loanApp.setEmploymentStatus("Salaried");
        loanApp.setMonthlyIncome(45000.0);
        loanApp.setCompanyName("Tech Corp");
        loanApp.setAddress("123 Main Street, Apt 4B");
        loanApp.setCity("New York");
        loanApp.setState("NY");
        loanApp.setPincode("10001");
        loanApp.setStatus("PENDING");
        
        // Save to database
        LoanApplication savedLoan = loanApplicationRepository.save(loanApp);
        
        // Verify it was saved
        assertThat(savedLoan.getId()).isNotNull();
        assertThat(savedLoan.getFullName()).isEqualTo("John Doe");
        assertThat(savedLoan.getStatus()).isEqualTo("PENDING");
        
        // Retrieve from database
        List<LoanApplication> applications = loanApplicationRepository.findByEmail("john.doe@example.com");
        assertThat(applications).isNotEmpty();
        assertThat(applications.get(0).getFullName()).isEqualTo("John Doe");
        
        System.out.println("Test passed: Loan application saved and retrieved successfully!");
        System.out.println("Application ID: " + savedLoan.getId());
    }
}