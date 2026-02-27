package com.sachin.webLoan.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sachin.webLoan.entity.ContactMessage;
import com.sachin.webLoan.entity.LoanApplication;
import com.sachin.webLoan.entity.User;
import com.sachin.webLoan.repository.ContactMessageRepository;
import com.sachin.webLoan.repository.LoanApplicationRepository;
import com.sachin.webLoan.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {
    
    @Autowired
    private ContactMessageRepository contactMessageRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private LoanApplicationRepository loanApplicationRepository;
    
    // Home page (Landing Page)
    @GetMapping("/")
    public String home() {
        return "index";
    }
    
    // Loan types page
    @GetMapping("/loan-types")
    public String loanTypes() {
        return "loan-types";
    }
    
    // About page
    @GetMapping("/about")
    public String about() {
        return "about";
    }
    
    // Contact page
    @GetMapping("/contact")
    public String contact(Model model) {
        model.addAttribute("contactMessage", new ContactMessage());
        return "contact";
    }
    
// Handle contact form submission - placed right before logout
    @PostMapping("/submit-contact")
    public String handleContact(@RequestParam("fullName") String fullName,
                               @RequestParam("email") String email,
                               @RequestParam(value = "phone", required = false) String phone,
                               @RequestParam("subject") String subject,
                               @RequestParam("message") String message,
                               RedirectAttributes redirectAttributes) {
        System.out.println("========== CONTACT FORM SUBMITTED ==========");
        System.out.println("Full Name: " + fullName);
        System.out.println("Email: " + email);
        System.out.println("Subject: " + subject);
        try {
            if (fullName == null || fullName.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Full name is required.");
                return "redirect:/contact";
            }
            if (email == null || email.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Email is required.");
                return "redirect:/contact";
            }
            if (subject == null || subject.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Subject is required.");
                return "redirect:/contact";
            }
            if (message == null || message.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Message is required.");
                return "redirect:/contact";
            }

            ContactMessage contactMessage = new ContactMessage();
            contactMessage.setFullName(fullName.trim());
            contactMessage.setEmail(email.trim());
            contactMessage.setPhone(phone != null ? phone.trim() : null);
            contactMessage.setSubject(subject.trim());
            contactMessage.setMessage(message.trim());
            contactMessage.setStatus("NEW");
            
            contactMessageRepository.save(contactMessage);
            redirectAttributes.addFlashAttribute("success", "Thank you for your message! We will get back to you soon.");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Failed to send message: " + e.getMessage());
        }
        return "redirect:/contact";
    }
    
    // Public logout
    @PostMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("success", "You have been logged out successfully.");
        return "redirect:/";
    }
    
    // Customer dashboard page
    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        // Check if user is logged in
        String username = (String) session.getAttribute("username");
        if (username != null) {
            // Find user by username
            Optional<User> userOpt = userRepository.findByUsername(username);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                model.addAttribute("user", user);
                model.addAttribute("role", user.getRole());
                
                // Get user's loan applications
                List<LoanApplication> userApplications = loanApplicationRepository.findByEmail(user.getEmail());
                model.addAttribute("userApplications", userApplications);
                
                // Calculate stats
                long totalApplications = userApplications.size();
                long pendingCount = userApplications.stream().filter(app -> "PENDING".equals(app.getStatus())).count();
                long approvedCount = userApplications.stream().filter(app -> "APPROVED".equals(app.getStatus())).count();
                long rejectedCount = userApplications.stream().filter(app -> "REJECTED".equals(app.getStatus())).count();
                
                model.addAttribute("totalApplications", totalApplications);
                model.addAttribute("pendingCount", pendingCount);
                model.addAttribute("approvedCount", approvedCount);
                model.addAttribute("rejectedCount", rejectedCount);
                
                return "dashboard";
            }
        }
        // If not logged in, redirect to home
        return "redirect:/";
    }
    
    // Profile page
    @GetMapping("/profile")
    public String profile(Model model, HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username != null) {
            Optional<User> userOpt = userRepository.findByUsername(username);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                model.addAttribute("user", user);
                model.addAttribute("username", username);
                model.addAttribute("role", user.getRole());
                
                // Get user's loan applications for stats
                List<LoanApplication> allApplications = loanApplicationRepository.findAll();
                long applicationCount =allApplications.size();
                long approvedCount = allApplications.stream().filter(app -> "APPROVED".equals(app.getStatus())).count();
                long pendingCount = allApplications.stream().filter(app -> "PENDING".equals(app.getStatus())).count();
                
                model.addAttribute("applicationCount", applicationCount);
                model.addAttribute("approvedCount", approvedCount);
                model.addAttribute("pendingCount", pendingCount);
                
                return "profile";
            }
        }
        // If not logged in, redirect to home
        return "redirect:/";
    }
    
    // Public customer loan status page - search by phone number (also used for /loan-status on Railway)
    @GetMapping({"/customer", "/loan-status"})
public String customerView(
        @RequestParam(value = "phone", required = false) String phone,
        Model model) {

    model.addAttribute("searchPhone", phone);

    if (phone != null && !phone.trim().isEmpty()) {

        String normalizedInput = normalizePhone(phone);

        // ✅ Force exactly 10 digits only
        if (normalizedInput.length() != 10) {
            model.addAttribute("error", "Please enter valid 10 digit mobile number.");
            return "customer-view";
        }

        Optional<LoanApplication> appOpt =
                loanApplicationRepository.findFirstByPhone(normalizedInput);

        if (appOpt.isPresent()) {
            model.addAttribute("application", appOpt.get());
        } else {
            model.addAttribute("error", "No application found for this number.");
        }
    }

    return "customer-view";
}
    
    // Application details page
    @GetMapping("/application/{id}")
    public String viewApplicationDetails(@PathVariable Long id, Model model, HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username != null) {
            Optional<User> userOpt = userRepository.findByUsername(username);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                Optional<LoanApplication> appOpt = loanApplicationRepository.findById(id);
                if (appOpt.isPresent()) {
                    LoanApplication application = appOpt.get();
                    // Make sure the application belongs to the logged-in user
                    if (application.getEmail().equals(user.getEmail())) {
                        model.addAttribute("application", application);
                        model.addAttribute("role", user.getRole());
                        return "application-details";
                    }
                }
            }
        }
        // If not authorized or application not found, redirect to dashboard
        return "redirect:/dashboard";
    }
    
    // Show loan application form
    @GetMapping("/apply")
    public String showApplyForm(Model model, @RequestParam(required = false) String loanType) {
        LoanApplication loanApplication = new LoanApplication();
        if (loanType != null && !loanType.isEmpty()) {
            loanApplication.setLoanType(loanType);
        }
        model.addAttribute("loanApplication", loanApplication);
        return "apply";
    }
    
    // Handle loan application form submission
    @PostMapping("/apply")
    public String submitApplication(@ModelAttribute LoanApplication loanApplication, 
                                   Model model, 
                                   RedirectAttributes redirectAttributes) {
        try {
            // Validate required fields
            if (loanApplication.getFullName() == null || loanApplication.getFullName().trim().isEmpty()) {
                model.addAttribute("loanApplication", loanApplication);
                model.addAttribute("error", "Full name is required.");
                return "apply";
            }
            if (loanApplication.getEmail() == null || loanApplication.getEmail().trim().isEmpty()) {
                model.addAttribute("loanApplication", loanApplication);
                model.addAttribute("error", "Email is required.");
                return "apply";
            }
            if (loanApplication.getPhone() == null || loanApplication.getPhone().trim().isEmpty()) {
                model.addAttribute("loanApplication", loanApplication);
                model.addAttribute("error", "Phone number is required.");
                return "apply";
            }
            if (loanApplication.getLoanType() == null || loanApplication.getLoanType().trim().isEmpty()) {
                model.addAttribute("loanApplication", loanApplication);
                model.addAttribute("error", "Loan type is required.");
                return "apply";
            }
            if (loanApplication.getLoanAmount() == null || loanApplication.getLoanAmount() <= 0) {
                model.addAttribute("loanApplication", loanApplication);
                model.addAttribute("error", "Loan amount is required and must be greater than 0.");
                return "apply";
            }
            if (loanApplication.getEmploymentStatus() == null || loanApplication.getEmploymentStatus().trim().isEmpty()) {
                model.addAttribute("loanApplication", loanApplication);
                model.addAttribute("error", "Employment status is required.");
                return "apply";
            }
            if (loanApplication.getMonthlyIncome() == null || loanApplication.getMonthlyIncome() <= 0) {
                model.addAttribute("loanApplication", loanApplication);
                model.addAttribute("error", "Monthly income is required and must be greater than 0.");
                return "apply";
            }
            if (loanApplication.getAddress() == null || loanApplication.getAddress().trim().isEmpty()) {
                model.addAttribute("loanApplication", loanApplication);
                model.addAttribute("error", "Address is required.");
                return "apply";
            }
            if (loanApplication.getCity() == null || loanApplication.getCity().trim().isEmpty()) {
                model.addAttribute("loanApplication", loanApplication);
                model.addAttribute("error", "City is required.");
                return "apply";
            }
            if (loanApplication.getState() == null || loanApplication.getState().trim().isEmpty()) {
                model.addAttribute("loanApplication", loanApplication);
                model.addAttribute("error", "State is required.");
                return "apply";
            }
            if (loanApplication.getPincode() == null || loanApplication.getPincode().trim().isEmpty()) {
                model.addAttribute("loanApplication", loanApplication);
                model.addAttribute("error", "Pincode is required.");
                return "apply";
            }
            
            // Normalize phone to digits only before saving
            if (loanApplication.getPhone() != null) {
                loanApplication.setPhone(normalizePhone(loanApplication.getPhone()));
            }

            // Save the loan application
            loanApplication.setStatus("PENDING");
            loanApplicationRepository.save(loanApplication);
            
            redirectAttributes.addFlashAttribute("success", "Your loan application has been submitted successfully. Our team will review it and contact you shortly.");
            return "redirect:/apply";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Failed to submit application. Please try again.");
            model.addAttribute("loanApplication", loanApplication);
            return "apply";
        }
    }

    private String normalizePhone(String phone) {

        if (phone == null) return "";
    
        String digits = phone.replaceAll("[^0-9]", "");
    
        // Remove country code 91
        if (digits.startsWith("91") && digits.length() == 12) {
            digits = digits.substring(2);
        }
    
        // Remove leading 0
        if (digits.startsWith("0") && digits.length() == 11) {
            digits = digits.substring(1);
        }
    
        return digits;
    }
}