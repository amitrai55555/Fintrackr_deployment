package com.finance.service;

import com.finance.dto.TransactionDTO;
import com.finance.dto.ai.AiBankAccountDTO;
import com.finance.dto.ai.AiContextDTO;
import com.finance.dto.ai.AiGoalDTO;
import com.finance.dto.ai.AiInvestmentDTO;
import com.finance.dto.ai.AiInvestmentSummaryDTO;
import com.finance.dto.ai.AiProfileDTO;
import com.finance.dto.ai.AiTransactionDTO;
import com.finance.dto.ai.AiTransactionSummaryDTO;
import com.finance.entity.BankAccount;
import com.finance.entity.Goal;
import com.finance.entity.Investment;
import com.finance.entity.User;
import com.finance.repository.BankAccountRepository;
import com.finance.repository.InvestmentRepository;
import com.finance.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class AiContextService {

    private final UserRepository userRepository;
    private final BankAccountRepository bankAccountRepository;
    private final DashboardService dashboardService;
    private final GoalService goalService;
    private final InvestmentRepository investmentRepository;
    private final InvestmentService investmentService;

    public AiContextService(
            UserRepository userRepository,
            BankAccountRepository bankAccountRepository,
            DashboardService dashboardService,
            GoalService goalService,
            InvestmentRepository investmentRepository,
            InvestmentService investmentService
    ) {
        this.userRepository = userRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.dashboardService = dashboardService;
        this.goalService = goalService;
        this.investmentRepository = investmentRepository;
        this.investmentService = investmentService;
    }

    public AiContextDTO getContext(Long userId, LocalDate from, LocalDate to) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<TransactionDTO> rawTransactions = dashboardService.getTransactionsByDateRange(userId, from, to);
        List<Goal> goals = goalService.getAllGoalsByUserId(userId);
        List<Investment> investments = investmentRepository.findByUserIdOrderByDateDesc(userId);
        List<BankAccount> bankAccounts = bankAccountRepository.findByUser(user);
        Map<String, Object> insights = dashboardService.getFinancialInsights(userId);

        // Also compute income/expense totals for the requested date range
        // so the profile reflects the period the AI is actually asking about
        BigDecimal rangeIncome = rawTransactions.stream()
                .filter(txn -> "INCOME".equalsIgnoreCase(txn.getType()) || "CREDIT".equalsIgnoreCase(txn.getType()))
                .map(TransactionDTO::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal rangeExpenses = rawTransactions.stream()
                .filter(txn -> "EXPENSE".equalsIgnoreCase(txn.getType()) || "DEBIT".equalsIgnoreCase(txn.getType()))
                .map(TransactionDTO::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        AiContextDTO context = new AiContextDTO();
        context.setProfile(buildProfile(user, bankAccounts, insights, userId, rangeIncome, rangeExpenses));
        context.setTransactions(rawTransactions.stream().map(this::toAiTransaction).toList());
        context.setTransactionSummary(buildTransactionSummary(rawTransactions, from, to));
        context.setGoals(goals.stream().map(this::toAiGoal).toList());
        context.setInvestments(investments.stream().map(this::toAiInvestment).toList());
        context.setInvestmentSummary(buildInvestmentSummary(investments, insights, userId));
        return context;
    }

    private AiProfileDTO buildProfile(
            User user,
            List<BankAccount> bankAccounts,
            Map<String, Object> insights,
            Long userId,
            BigDecimal rangeIncome,
            BigDecimal rangeExpenses
    ) {
        AiProfileDTO profile = new AiProfileDTO();
        profile.setUserId(user.getId());
        profile.setUsername(user.getUsername());
        profile.setFirstName(user.getFirstName());
        profile.setLastName(user.getLastName());
        profile.setFullName(buildFullName(user));
        profile.setEmail(user.getEmail());

        // Use the date-range-specific totals so the AI sees the right numbers
        // for the period it asked about; fall back to current-month insights
        BigDecimal income = rangeIncome != null && rangeIncome.compareTo(BigDecimal.ZERO) > 0
                ? rangeIncome : asBigDecimal(insights.get("monthlyIncome"));
        BigDecimal expenses = rangeExpenses != null && rangeExpenses.compareTo(BigDecimal.ZERO) > 0
                ? rangeExpenses : asBigDecimal(insights.get("monthlyExpenses"));
        BigDecimal savings = income.subtract(expenses);

        profile.setMonthlyIncome(income);
        profile.setMonthlyExpenses(expenses);
        profile.setMonthlySavings(savings);
        profile.setMonthlyInvestmentCapacity(asBigDecimal(insights.get("investmentCapacity")));
        profile.setSuggestedRiskProfile(investmentService.determineRiskProfile(userId));
        profile.setBankAccounts(bankAccounts.stream().map(this::toAiBankAccount).toList());
        return profile;
    }

    private AiTransactionSummaryDTO buildTransactionSummary(
            List<TransactionDTO> transactions,
            LocalDate from,
            LocalDate to
    ) {
        BigDecimal totalIncome = transactions.stream()
                .filter(txn -> "INCOME".equalsIgnoreCase(txn.getType()) || "CREDIT".equalsIgnoreCase(txn.getType()))
                .map(TransactionDTO::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpenses = transactions.stream()
                .filter(txn -> "EXPENSE".equalsIgnoreCase(txn.getType()) || "DEBIT".equalsIgnoreCase(txn.getType()))
                .map(TransactionDTO::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        AiTransactionSummaryDTO summary = new AiTransactionSummaryDTO();
        summary.setFrom(from);
        summary.setTo(to);
        summary.setTotalIncome(totalIncome);
        summary.setTotalExpenses(totalExpenses);
        summary.setNetCashFlow(totalIncome.subtract(totalExpenses));
        summary.setTransactionCount(transactions.size());
        return summary;
    }

    private AiInvestmentSummaryDTO buildInvestmentSummary(
            List<Investment> investments,
            Map<String, Object> insights,
            Long userId
    ) {
        BigDecimal totalInvested = investments.stream()
                .map(Investment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        AiInvestmentSummaryDTO summary = new AiInvestmentSummaryDTO();
        summary.setTotalInvestedAmount(totalInvested);
        summary.setMonthlyInvestmentCapacity(asBigDecimal(insights.get("investmentCapacity")));
        summary.setMonthlySavings(asBigDecimal(insights.get("monthlySavings")));
        summary.setSuggestedRiskProfile(investmentService.determineRiskProfile(userId));
        summary.setInvestmentCount(investments.size());
        return summary;
    }

    private AiBankAccountDTO toAiBankAccount(BankAccount account) {
        AiBankAccountDTO dto = new AiBankAccountDTO();
        dto.setId(account.getId());
        dto.setBankName(account.getBankName());
        dto.setAccountHolderName(account.getAccountHolderName());
        dto.setAccountType(account.getAccountType());
        dto.setIfscCode(account.getIfscCode());
        dto.setVerified(account.isVerified());
        return dto;
    }

    private AiTransactionDTO toAiTransaction(TransactionDTO transaction) {
        AiTransactionDTO dto = new AiTransactionDTO();
        dto.setId(transaction.getId());
        dto.setType(transaction.getType());
        dto.setDescription(transaction.getDescription());
        dto.setCategory(transaction.getCategory());
        dto.setDate(transaction.getDate());
        dto.setAmount(transaction.getAmount());
        return dto;
    }

    private AiGoalDTO toAiGoal(Goal goal) {
        AiGoalDTO dto = new AiGoalDTO();
        dto.setId(goal.getId());
        dto.setTitle(goal.getTitle());
        dto.setDescription(goal.getDescription());
        dto.setTargetAmount(goal.getTargetAmount());
        dto.setCurrentAmount(goal.getCurrentAmount());
        dto.setMonthlyContribution(goal.getMonthlyContribution());
        dto.setTargetDate(goal.getTargetDate());
        dto.setPriority(goal.getPriority() == null ? null : goal.getPriority().name());
        dto.setStatus(goal.getStatus() == null ? null : goal.getStatus().name());
        dto.setCompletionDate(goal.getCompletionDate());
        dto.setProgressPercentage(goal.getProgressPercentage());
        return dto;
    }

    private AiInvestmentDTO toAiInvestment(Investment investment) {
        AiInvestmentDTO dto = new AiInvestmentDTO();
        dto.setId(investment.getId());
        dto.setName(investment.getName());
        dto.setTickerSymbol(investment.getTickerSymbol());
        dto.setType(investment.getType());
        dto.setAmount(investment.getAmount());
        dto.setQuantity(investment.getQuantity());
        dto.setDate(investment.getDate());
        dto.setBroker(investment.getBroker());
        dto.setDescription(investment.getDescription());
        return dto;
    }

    private String buildFullName(User user) {
        String first = user.getFirstName() == null ? "" : user.getFirstName().trim();
        String last = user.getLastName() == null ? "" : user.getLastName().trim();
        String fullName = (first + " " + last).trim();
        return fullName.isEmpty() ? user.getUsername() : fullName;
    }

    private BigDecimal asBigDecimal(Object value) {
        return value instanceof BigDecimal decimal ? decimal : BigDecimal.ZERO;
    }
}
