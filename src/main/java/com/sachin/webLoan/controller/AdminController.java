package com.sachin.webLoan.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.HashMap;
import java.util.Map;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    // ================= ADMIN LOGIN =================
    @GetMapping("/login")
    public String adminLogin(HttpSession session) {
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

                    session.setAttribute("adminUser", user);
                    redirectAttributes.addFlashAttribute("success", "Login successful!");
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

    // ================= DASHBOARD =================
    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {

        if (!isAdminLoggedIn(session)) {
            return "redirect:/admin/login";
        }

        try {
            List<LoanApplication> allApplications = loanApplicationRepository.findAll();
            List<LoanApplication> approvedApps = loanApplicationRepository.findByStatus("APPROVED");
            List<LoanApplication> pendingApps = loanApplicationRepository.findByStatus("PENDING");

            if (allApplications == null) allApplications = new ArrayList<>();
            if (approvedApps == null) approvedApps = new ArrayList<>();
            if (pendingApps == null) pendingApps = new ArrayList<>();

            model.addAttribute("totalApplications", allApplications.size());
            model.addAttribute("totalUsers", (int) userRepository.count());
            model.addAttribute("approvedApplications", approvedApps.size());
            model.addAttribute("pendingApplications", pendingApps.size());

            model.addAttribute("recentApplications", allApplications);
            return "admin-dashboard";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("totalApplications", 0);
            model.addAttribute("totalUsers", 0);
            model.addAttribute("approvedApplications", 0);
            model.addAttribute("pendingApplications", 0);
            model.addAttribute("recentApplications", new ArrayList<>());
            return "admin-dashboard";
        }
    }

    // ================= APPLICATION LIST =================
    @GetMapping("/applications")
    public String applications(Model model, HttpSession session) {

        if (!isAdminLoggedIn(session)) {
            return "redirect:/admin/login";
        }

        List<LoanApplication> allApplications = loanApplicationRepository.findAll();
        model.addAttribute("allApplications", allApplications);
        model.addAttribute("totalApplications", allApplications.size());
        return "admin-applications";
    }

    // ================= VIEW SINGLE APPLICATION =================
    @GetMapping("/application/{id}")
    public String viewApplication(@PathVariable Long id, Model model, HttpSession session) {

        if (!isAdminLoggedIn(session)) {
            return "redirect:/admin/login";
        }

        Optional<LoanApplication> appOpt = loanApplicationRepository.findById(id);
        if (appOpt.isPresent()) {
            model.addAttribute("application", appOpt.get());
            return "admin-application-detail";
        }
        return "redirect:/admin/applications";
    }

    // ================= SAVE ADMIN REPLY (IMPORTANT) =================
    @PostMapping("/application/{id}/reply")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> saveAdminReply(@PathVariable Long id,
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
        String reply = request.get("reply");

        if (reply != null && !reply.trim().isEmpty()) {
            application.setAdminReply(reply);
            loanApplicationRepository.save(application);

            result.put("success", true);
            result.put("message", "Reply saved successfully");
        } else {
            result.put("success", false);
            result.put("message", "Reply message cannot be empty");
        }

        return ResponseEntity.ok(result);
    }

    // ================= UPDATE APPLICATION STATUS =================
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

    // ================= DELETE APPLICATION =================
    @DeleteMapping("/application/{id}/delete")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteApplication(@PathVariable Long id,
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
        
        try {
            LoanApplication application = appOpt.get();
            String applicantName = application.getFullName();
            
            // Delete the application
            loanApplicationRepository.deleteById(id);
            
            result.put("success", true);
            result.put("message", "Application for " + applicantName + " deleted successfully");
            
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "Failed to delete application: " + e.getMessage());
        }
        
        return ResponseEntity.ok(result);
    }
    
    // ================= LOGOUT =================
    @PostMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.removeAttribute("adminUser");
        redirectAttributes.addFlashAttribute("success", "Logged out successfully.");
        return "redirect:/";
    }

    // ================= SECURITY CHECK =================
    private boolean isAdminLoggedIn(HttpSession session) {
        User adminUser = (User) session.getAttribute("adminUser");
        return adminUser != null && "ADMIN".equals(adminUser.getRole());
    }
// ================= ADMIN MESSAGES PAGE =================
@GetMapping("/messages")
public String adminMessages(Model model, HttpSession session) {

    if (!isAdminLoggedIn(session)) {
        return "redirect:/admin/login";
    }

    List<ContactMessage> allMessages = contactMessageRepository.findAll();
    model.addAttribute("allMessages", allMessages);
    model.addAttribute("totalMessages", allMessages.size());

    return "admin-messages"; // matches your HTML file name
}

// ================= GET SINGLE MESSAGE (FOR MODAL) =================
@GetMapping("/message/{id}")
@ResponseBody
public ResponseEntity<Map<String, Object>> getMessageById(@PathVariable Long id,
                                                         HttpSession session) {

    Map<String, Object> result = new HashMap<>();

    if (!isAdminLoggedIn(session)) {
        result.put("success", false);
        result.put("message", "Not authorized");
        return ResponseEntity.status(401).body(result);
    }

    Optional<ContactMessage> msgOpt = contactMessageRepository.findById(id);

    if (msgOpt.isEmpty()) {
        result.put("success", false);
        result.put("message", "Message not found");
        return ResponseEntity.badRequest().body(result);
    }

    ContactMessage msg = msgOpt.get();

    Map<String, Object> data = new HashMap<>();
    data.put("id", msg.getId());
    data.put("fullName", msg.getFullName());
    data.put("email", msg.getEmail());
    data.put("phone", msg.getPhone());
    data.put("subject", msg.getSubject());
    data.put("message", msg.getMessage());
    data.put("status", msg.getStatus());
    data.put("createdDate", msg.getCreatedDate() != null
            ? msg.getCreatedDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))
            : "N/A");

    result.put("success", true);
    result.put("data", data);

    return ResponseEntity.ok(result);
}

// ================= MARK MESSAGE AS READ =================
@PostMapping("/message/{id}/read")
@ResponseBody
public ResponseEntity<Map<String, Object>> markMessageAsRead(@PathVariable Long id,
                                                             HttpSession session) {

    Map<String, Object> result = new HashMap<>();

    if (!isAdminLoggedIn(session)) {
        result.put("success", false);
        result.put("message", "Not authorized");
        return ResponseEntity.status(401).body(result);
    }

    Optional<ContactMessage> msgOpt = contactMessageRepository.findById(id);

    if (msgOpt.isEmpty()) {
        result.put("success", false);
        result.put("message", "Message not found");
        return ResponseEntity.badRequest().body(result);
    }

    ContactMessage msg = msgOpt.get();
    msg.setStatus("READ");
    contactMessageRepository.save(msg);

    result.put("success", true);
    result.put("message", "Message marked as read");

    return ResponseEntity.ok(result);
}

// ================= ADMIN USERS PAGE =================
@GetMapping("/users")
public String adminUsers(Model model, HttpSession session) {

    if (!isAdminLoggedIn(session)) {
        return "redirect:/admin/login";
    }

    List<User> users = userRepository.findAll();
    
    if (users == null) {
        users = new ArrayList<>();
    }

    // 🔥 CHANGE THIS LINE
    model.addAttribute("allUsers", users); // instead of "users"
    model.addAttribute("totalUsers", users.size());

    return "admin-users";
}
}