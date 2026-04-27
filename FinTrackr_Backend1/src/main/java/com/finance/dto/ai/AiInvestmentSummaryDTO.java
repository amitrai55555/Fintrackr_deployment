package com.finance.dto.ai;

import java.math.BigDecimal;

public class AiInvestmentSummaryDTO {

    private BigDecimal totalInvestedAmount;
    private BigDecimal monthlyInvestmentCapacity;
    private BigDecimal monthlySavings;
    private String suggestedRiskProfile;
    private int investmentCount;

    public BigDecimal getTotalInvestedAmount() {
        return totalInvestedAmount;
    }

    public void setTotalInvestedAmount(BigDecimal totalInvestedAmount) {
        this.totalInvestedAmount = totalInvestedAmount;
    }

    public BigDecimal getMonthlyInvestmentCapacity() {
        return monthlyInvestmentCapacity;
    }

    public void setMonthlyInvestmentCapacity(BigDecimal monthlyInvestmentCapacity) {
        this.monthlyInvestmentCapacity = monthlyInvestmentCapacity;
    }

    public BigDecimal getMonthlySavings() {
        return monthlySavings;
    }

    public void setMonthlySavings(BigDecimal monthlySavings) {
        this.monthlySavings = monthlySavings;
    }

    public String getSuggestedRiskProfile() {
        return suggestedRiskProfile;
    }

    public void setSuggestedRiskProfile(String suggestedRiskProfile) {
        this.suggestedRiskProfile = suggestedRiskProfile;
    }

    public int getInvestmentCount() {
        return investmentCount;
    }

    public void setInvestmentCount(int investmentCount) {
        this.investmentCount = investmentCount;
    }
}
