package com.sachin.webLoan.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import com.sachin.webLoan.entity.ContactMessage;
import com.sachin.webLoan.entity.LoanApplication;
import com.sachin.webLoan.entity.User;
import com.sachin.webLoan.repository.ContactMessageRepository;
import com.sachin.webLoan.repository.LoanApplicationRepository;
import com.sachin.webLoan.repository.UserRepository;
import com.sachin.webLoan.util.SimplePasswordEncoder;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private LoanApplicationRepository loanApplicationRepository;
    
    @Autowired
    private ContactMessageRepository contactMessageRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private SimplePasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String adminLogin(HttpSession session) {
        // Check if admin is already logged in
        if (session.getAttribute("adminUser") != null) {
            return "redirect:/admin/dashboard";
        }
        return "admin-login";
    }
    
    @PostMapping("/login")
    public String handleAdminLogin(@RequestParam String username, 
                                 @RequestParam String password,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        try {
            Optional<User> userOpt = userRepository.findByUsername(username);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                if ("ADMIN".equals(user.getRole()) && 
                    passwordEncoder.matches(password, user.getPassword())) {
                    // Login successful - store user in session
                    session.setAttribute("adminUser", user);
                    redirectAttributes.addFlashAttribute("success", "Login successful! Welcome to admin dashboard.");
                    return "redirect:/admin/dashboard";
                }
            }
            redirectAttributes.addFlashAttribute("error", "Invalid admin credentials");
            return "redirect:/admin/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Login failed. Please try again.");
            return "redirect:/admin/login";
        }
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        try {
            // Check if admin is logged in
            User adminUser = (User) session.getAttribute("adminUser");
            if (adminUser != null && "ADMIN".equals(adminUser.getRole())) {
                try {
                    // Add dashboard statistics
                    List<LoanApplication> allApplications = loanApplicationRepository.findAll();
                    List<LoanApplication> approvedApps = loanApplicationRepository.findByStatus("APPROVED");
                    List<LoanApplication> pendingApps = loanApplicationRepository.findByStatus("PENDING");
                    
                    // Ensure we have valid lists
                    if (allApplications == null) allApplications = new ArrayList<>();
                    if (approvedApps == null) approvedApps = new ArrayList<>();
                    if (pendingApps == null) pendingApps = new ArrayList<>();
                    
                    model.addAttribute("totalApplications", allApplications.size());
                    model.addAttribute("totalUsers", (int) userRepository.count());
                    model.addAttribute("approvedApplications", approvedApps.size());
                    model.addAttribute("pendingApplications", pendingApps.size());
                    
                    // Handle potential null values in stream operations
                    List<LoanApplication> recentApps = new ArrayList<>();
                    if (allApplications != null && !allApplications.isEmpty()) {
                        recentApps = allApplications.stream()
                            .filter(app -> app != null && app.getApplicationDate() != null) // Filter out null applications and those with null dates
                            .sorted((a, b) -> {
                                if (a.getApplicationDate() == null && b.getApplicationDate() == null) return 0;
                                if (a.getApplicationDate() == null) return 1;
                                if (b.getApplicationDate() == null) return -1;
                                return b.getApplicationDate().compareTo(a.getApplicationDate());
                            })
                            .limit(5)
                            .collect(java.util.stream.Collectors.toList());
                    }
                    
                    model.addAttribute("recentApplications", recentApps);
                    return "admin-dashboard";
                } catch (Exception e) {
                    e.printStackTrace();
                    // If there's an error with data retrieval, still show dashboard but with minimal data
                    model.addAttribute("totalApplications", 0);
                    model.addAttribute("totalUsers", 0);
                    model.addAttribute("approvedApplications", 0);
                    model.addAttribute("pendingApplications", 0);
                    model.addAttribute("recentApplications", new ArrayList<>());
                    return "admin-dashboard";
                }
            }
            // If not admin, redirect to admin login
            return "redirect:/admin/login";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/admin/login";
        }
    }
    
    // Other methods remain unchanged for now...
    
    @GetMapping("/applications")
    public String applications(Model model, HttpSession session) {
        try {
            // Check if admin is logged in
            User adminUser = (User) session.getAttribute("adminUser");
            if (adminUser != null && "ADMIN".equals(adminUser.getRole())) {
                // Get all loan applications
                List<LoanApplication> allApplications = loanApplicationRepository.findAll();
                
                model.addAttribute("allApplications", allApplications);
                model.addAttribute("totalApplications", allApplications.size());
                return "admin-applications";
            }
            // If not admin, redirect to admin login
            return "redirect:/admin/login";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/admin/login";
        }
    }
    
    @GetMapping("/application/{id}")
    public String viewApplication(@PathVariable Long id, Model model, HttpSession session) {
        try {
            // Check if admin is logged in
            User adminUser = (User) session.getAttribute("adminUser");
            if (adminUser != null && "ADMIN".equals(adminUser.getRole())) {
                Optional<LoanApplication> appOpt = loanApplicationRepository.findById(id);
                if (appOpt.isPresent()) {
                    model.addAttribute("application", appOpt.get());
                    return "admin-application-detail";
                }
                return "redirect:/admin/applications";
            }
            // If not admin, redirect to admin login
            return "redirect:/admin/login";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/admin/login";
        }
    }
    
    @GetMapping("/messages")
    public String messages(Model model, HttpSession session) {
        try {
            // Check if admin is logged in
            User adminUser = (User) session.getAttribute("adminUser");
            if (adminUser != null && "ADMIN".equals(adminUser.getRole())) {
                // Get all contact messages
                List<ContactMessage> allMessages = contactMessageRepository.findAll();
                
                model.addAttribute("allMessages", allMessages);
                model.addAttribute("totalMessages", allMessages.size());
                return "admin-messages";
            }
            // If not admin, redirect to admin login
            return "redirect:/admin/login";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/admin/login";
        }
    }
    
    @GetMapping("/users")
    public String users(Model model, HttpSession session) {
        try {
            // Check if admin is logged in
            User adminUser = (User) session.getAttribute("adminUser");
            if (adminUser != null && "ADMIN".equals(adminUser.getRole())) {
                // Get all users
                List<User> allUsers = userRepository.findAll();
                
                model.addAttribute("allUsers", allUsers);
                model.addAttribute("totalUsers", allUsers.size());
                return "admin-users";
            }
            // If not admin, redirect to admin login
            return "redirect:/admin/login";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/admin/login";
        }
    }

    @PostMapping("/users/create")
    public String createAdminUser(@RequestParam String fullName,
                                  @RequestParam String username,
                                  @RequestParam String email,
                                  @RequestParam String password,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        if (!isAdminLoggedIn(session)) {
            return "redirect:/admin/login";
        }

        try {
            if (userRepository.existsByUsername(username)) {
                redirectAttributes.addFlashAttribute("error", "Username already exists.");
                return "redirect:/admin/users";
            }

            User user = new User();
            user.setFullName(fullName);
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setRole("ADMIN");
            user.setIsActive(true);

            userRepository.save(user);
            redirectAttributes.addFlashAttribute("success", "Admin user created successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to create admin user. Please try again.");
        }

        return "redirect:/admin/users";
    }

    @PostMapping("/user/{userId}/toggle-status")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> toggleUserStatus(@PathVariable Long userId, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        if (!isAdminLoggedIn(session)) {
            result.put("success", false);
            result.put("message", "Not authorized");
            return ResponseEntity.status(401).body(result);
        }
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            result.put("success", false);
            result.put("message", "User not found");
            return ResponseEntity.badRequest().body(result);
        }
        User user = userOpt.get();
        user.setIsActive(Boolean.FALSE.equals(user.getIsActive()));
        userRepository.save(user);
        result.put("success", true);
        result.put("message", "Status updated to " + (user.getIsActive() ? "ACTIVE" : "INACTIVE"));
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/message/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getMessage(@PathVariable Long id, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        if (!isAdminLoggedIn(session)) {
            result.put("success", false);
            result.put("message", "Not authorized");
            return ResponseEntity.status(401).body(result);
        }
        Optional<ContactMessage> messageOpt = contactMessageRepository.findById(id);
        if (messageOpt.isEmpty()) {
            result.put("success", false);
            result.put("message", "Message not found");
            return ResponseEntity.badRequest().body(result);
        }
        ContactMessage message = messageOpt.get();
        Map<String, Object> messageData = new HashMap<>();
        messageData.put("id", message.getId());
        messageData.put("fullName", message.getFullName());
        messageData.put("email", message.getEmail());
        messageData.put("phone", message.getPhone() != null ? message.getPhone() : "N/A");
        messageData.put("subject", message.getSubject());
        messageData.put("message", message.getMessage());
        messageData.put("status", message.getStatus() != null ? message.getStatus() : "NEW");
        if (message.getCreatedDate() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
            messageData.put("createdDate", message.getCreatedDate().format(formatter));
        } else {
            messageData.put("createdDate", "N/A");
        }
        result.put("success", true);
        result.put("data", messageData);
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/application/{id}/status")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateApplicationStatus(@PathVariable Long id,
                                                                       @RequestBody Map<String, String> request,
                                                                       HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        if (!isAdminLoggedIn(session)) {
            result.put("success", false);
            result.put("message", "Not authorized");
            return ResponseEntity.status(401).body(result);
        }
        Optional<LoanApplication> appOpt = loanApplicationRepository.findById(id);
        if (appOpt.isEmpty()) {
            result.put("success", false);
            result.put("message", "Application not found");
            return ResponseEntity.badRequest().body(result);
        }
        LoanApplication application = appOpt.get();
        String status = request.get("status");
        if (status != null && !status.isEmpty()) {
            application.setStatus(status);
            loanApplicationRepository.save(application);
            result.put("success", true);
            result.put("message", "Status updated successfully");
        } else {
            result.put("success", false);
            result.put("message", "Status is required");
        }
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/message/{id}/read")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> markMessageAsRead(@PathVariable Long id, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        if (!isAdminLoggedIn(session)) {
            result.put("success", false);
            result.put("message", "Not authorized");
            return ResponseEntity.status(401).body(result);
        }
        Optional<ContactMessage> messageOpt = contactMessageRepository.findById(id);
        if (messageOpt.isEmpty()) {
            result.put("success", false);
            result.put("message", "Message not found");
            return ResponseEntity.badRequest().body(result);
        }
        ContactMessage message = messageOpt.get();
        message.setStatus("READ");
        contactMessageRepository.save(message);
        result.put("success", true);
        result.put("message", "Message marked as read");
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.removeAttribute("adminUser");
        redirectAttributes.addFlashAttribute("success", "You have been logged out successfully.");
        return "redirect:/";
    }

    private boolean isAdminLoggedIn(HttpSession session) {
        User adminUser = (User) session.getAttribute("adminUser");
        return adminUser != null && "ADMIN".equals(adminUser.getRole());
    }
}