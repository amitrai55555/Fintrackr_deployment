package com.finance.dto.ai;

import java.util.ArrayList;
import java.util.List;

public class AiContextDTO {

    private AiProfileDTO profile;
    private AiTransactionSummaryDTO transactionSummary;
    private AiInvestmentSummaryDTO investmentSummary;
    private List<AiTransactionDTO> transactions = new ArrayList<>();
    private List<AiGoalDTO> goals = new ArrayList<>();
    private List<AiInvestmentDTO> investments = new ArrayList<>();

    public AiProfileDTO getProfile() {
        return profile;
    }

    public void setProfile(AiProfileDTO profile) {
        this.profile = profile;
    }

    public AiTransactionSummaryDTO getTransactionSummary() {
        return transactionSummary;
    }

    public void setTransactionSummary(AiTransactionSummaryDTO transactionSummary) {
        this.transactionSummary = transactionSummary;
    }

    public AiInvestmentSummaryDTO getInvestmentSummary() {
        return investmentSummary;
    }

    public void setInvestmentSummary(AiInvestmentSummaryDTO investmentSummary) {
        this.investmentSummary = investmentSummary;
    }

    public List<AiTransactionDTO> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<AiTransactionDTO> transactions) {
        this.transactions = transactions;
    }

    public List<AiGoalDTO> getGoals() {
        return goals;
    }

    public void setGoals(List<AiGoalDTO> goals) {
        this.goals = goals;
    }

    public List<AiInvestmentDTO> getInvestments() {
        return investments;
    }

    public void setInvestments(List<AiInvestmentDTO> investments) {
        this.investments = investments;
    }
}
