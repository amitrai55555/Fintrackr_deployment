package com.finance.dto.ai;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AiProfileDTO {

    private Long userId;
    private String username;
    private String firstName;
    private String lastName;
    private String fullName;
    private String email;
    private BigDecimal monthlyIncome;
    private BigDecimal monthlyExpenses;
    private BigDecimal monthlySavings;
    private BigDecimal monthlyInvestmentCapacity;
    private String suggestedRiskProfile;
    private List<AiBankAccountDTO> bankAccounts = new ArrayList<>();

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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

    public BigDecimal getMonthlyIncome() {
        return monthlyIncome;
    }

    public void setMonthlyIncome(BigDecimal monthlyIncome) {
        this.monthlyIncome = monthlyIncome;
    }

    public BigDecimal getMonthlyExpenses() {
        return monthlyExpenses;
    }

    public void setMonthlyExpenses(BigDecimal monthlyExpenses) {
        this.monthlyExpenses = monthlyExpenses;
    }

    public BigDecimal getMonthlySavings() {
        return monthlySavings;
    }

    public void setMonthlySavings(BigDecimal monthlySavings) {
        this.monthlySavings = monthlySavings;
    }

    public BigDecimal getMonthlyInvestmentCapacity() {
        return monthlyInvestmentCapacity;
    }

    public void setMonthlyInvestmentCapacity(BigDecimal monthlyInvestmentCapacity) {
        this.monthlyInvestmentCapacity = monthlyInvestmentCapacity;
    }

    public String getSuggestedRiskProfile() {
        return suggestedRiskProfile;
    }

    public void setSuggestedRiskProfile(String suggestedRiskProfile) {
        this.suggestedRiskProfile = suggestedRiskProfile;
    }

    public List<AiBankAccountDTO> getBankAccounts() {
        return bankAccounts;
    }

    public void setBankAccounts(List<AiBankAccountDTO> bankAccounts) {
        this.bankAccounts = bankAccounts;
    }
}
