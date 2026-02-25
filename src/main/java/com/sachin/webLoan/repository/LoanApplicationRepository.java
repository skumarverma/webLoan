package com.sachin.webLoan.repository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.sachin.webLoan.entity.LoanApplication;

@Repository
public interface LoanApplicationRepository extends JpaRepository<LoanApplication, Long> {

    List<LoanApplication> findByStatus(String status);
    List<LoanApplication> findByEmail(String email);
    List<LoanApplication> findByLoanType(String loanType);

    Optional<LoanApplication> findByPhone(String phone); // ⭐ Important
}