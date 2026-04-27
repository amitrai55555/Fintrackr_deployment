package com.finance.repository;

import com.finance.entity.BankAccount;
import com.finance.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {


    List<BankAccount> findByUser(User user);


    List<BankAccount> findByUserAndIsVerifiedTrue(User user);


    Optional<BankAccount> findByIdAndUser(Long id, User user);


    boolean existsByAccountNumberEncrypted(String accountNumberEncrypted);


    List<BankAccount> findByIfscCode(String ifscCode);

    Optional<BankAccount> findPrimaryVerifiedAccountByUserId(Long userId);
    void deleteByUser(User user);
    Optional<BankAccount> findByIdAndUserId(Long id, Long userId);


}
