package com.sachin.webLoan.controller;

import com.sachin.webLoan.entity.LoanApplication; // ✅ FIXED (entity not model)
import com.sachin.webLoan.repository.LoanApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class LoanStatusController {

    @Autowired
    private LoanApplicationRepository loanRepository;

    // Open Loan Status Page
    @GetMapping("/loan-status")
    public String loanStatusPage(Model model) {
        // Initialize with empty state to avoid null issues in template
        model.addAttribute("showForm", true);
        return "loan-status";
    }

    // Check phone and show status + admin reply
    @PostMapping("/loan-status")
public String checkLoanStatus(@RequestParam("phone") String phone, Model model) {

    try {
        // 🔥 CLEAN PHONE NUMBER (remove spaces, +91, dashes etc)
        String cleanPhone = phone.replaceAll("[^0-9]", "");

        Optional<LoanApplication> loanOptional =
                loanRepository.findFirstByPhone(cleanPhone);

        if (loanOptional.isPresent()) {
            LoanApplication loan = loanOptional.get();
            model.addAttribute("loan", loan);
        } else {
            model.addAttribute("error",
                    "Phone number not found. Please enter correct registered number.");
        }

    } catch (Exception e) {
        e.printStackTrace();
        model.addAttribute("error", "Something went wrong. Please try again.");
    }

    return "loan-status";
}
    }