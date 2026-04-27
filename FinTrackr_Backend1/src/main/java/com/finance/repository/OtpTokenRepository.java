package com.finance.repository;

import com.finance.entity.BankAccount;
import com.finance.entity.OtpToken;
import com.finance.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface OtpTokenRepository extends JpaRepository<OtpToken, Long> {


    Optional<OtpToken> findTopByUserAndBankAccountAndUsedFalseOrderByCreatedAtDesc(
            User user,
            BankAccount bankAccount
    );


//    void deleteByExpiryTimeBefore(java.time.LocalDateTime time);
//    void deleteByUser(User user);
    void deleteByBankAccount(BankAccount bankAccount);




}
