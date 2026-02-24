package com.sachin.webLoan.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "loan_applications")
public class LoanApplication {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "full_name", nullable = false)
    private String fullName;
    
    @Column(name = "email", nullable = false)
    private String email;
    
    @Column(name = "phone", nullable = false)
    private String phone;
    
    @Column(name = "loan_type", nullable = false)
    private String loanType;
    
    @Column(name = "loan_amount", nullable = false)
    private Double loanAmount;
    
    @Column(name = "employment_status", nullable = false)
    private String employmentStatus;
    
    @Column(name = "monthly_income", nullable = false)
    private Double monthlyIncome;
    
    @Column(name = "company_name")
    private String companyName;
    
    @Column(name = "address", nullable = false, length = 1000)
    private String address;
    
    @Column(name = "city", nullable = false)
    private String city;
    
    @Column(name = "state", nullable = false)
    private String state;
    
    @Column(name = "pincode", nullable = false)
    private String pincode;
    
    @Column(name = "application_date")
    private LocalDateTime applicationDate;
    
    @Column(name = "status")
    private String status = "PENDING";
    
    // Getter and setter for status
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    // Default constructor
    public LoanApplication() {}
    
    // Constructor with all parameters
    public LoanApplication(Long id, String fullName, String email, String phone, String loanType, 
                         Double loanAmount, String employmentStatus, Double monthlyIncome, 
                         String companyName, String address, String city, String state, 
                         String pincode, LocalDateTime applicationDate, String status) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.loanType = loanType;
        this.loanAmount = loanAmount;
        this.employmentStatus = employmentStatus;
        this.monthlyIncome = monthlyIncome;
        this.companyName = companyName;
        this.address = address;
        this.city = city;
        this.state = state;
        this.pincode = pincode;
        this.applicationDate = applicationDate;
        this.status = status;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getLoanType() {
        return loanType;
    }
    
    public void setLoanType(String loanType) {
        this.loanType = loanType;
    }
    
    public Double getLoanAmount() {
        return loanAmount;
    }
    
    public void setLoanAmount(Double loanAmount) {
        this.loanAmount = loanAmount;
    }
    
    public String getEmploymentStatus() {
        return employmentStatus;
    }
    
    public void setEmploymentStatus(String employmentStatus) {
        this.employmentStatus = employmentStatus;
    }
    
    public Double getMonthlyIncome() {
        return monthlyIncome;
    }
    
    public void setMonthlyIncome(Double monthlyIncome) {
        this.monthlyIncome = monthlyIncome;
    }
    
    public String getCompanyName() {
        return companyName;
    }
    
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getCity() {
        return city;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    
    public String getState() {
        return state;
    }
    
    public void setState(String state) {
        this.state = state;
    }
    
    public String getPincode() {
        return pincode;
    }
    
    public void setPincode(String pincode) {
        this.pincode = pincode;
    }
    
    public LocalDateTime getApplicationDate() {
        return applicationDate;
    }
    
    public void setApplicationDate(LocalDateTime applicationDate) {
        this.applicationDate = applicationDate;
    }
    
    @PrePersist
    protected void onCreate() {
        applicationDate = LocalDateTime.now();
    }
}
