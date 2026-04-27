package com.finance.controller;

import com.finance.dto.PortfolioAllocation;
import com.finance.security.UserPrincipal;
import com.finance.service.InvestmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import com.finance.dto.InvestmentRequest;
import com.finance.entity.Investment;
import com.finance.entity.User;
import com.finance.repository.InvestmentRepository;
import com.finance.repository.UserRepository;
import com.finance.service.MarketDataService;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/investments")
@CrossOrigin(origins = "*", maxAge = 3600)
public class InvestmentController {

    @Autowired
    private InvestmentService investmentService;

    @Autowired
    private InvestmentRepository investmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MarketDataService marketDataService;

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getInvestments(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        List<Investment> savedInvestments = investmentRepository.findByUserIdOrderByDateDesc(userPrincipal.getId());
        
        List<Map<String, Object>> result = new ArrayList<>();
        
        for (Investment inv : savedInvestments) {
            Map<String, Object> map = new HashMap<>();
            
            String name = inv.getName();
            String ticker = inv.getTickerSymbol();
            
            // Extract ticker from name if missing (e.g. old data or manual input)
            if ((ticker == null || ticker.trim().isEmpty()) && name != null && name.contains(" - ")) {
                String[] parts = name.split(" - ", 2);
                if (parts.length == 2 && parts[0].length() <= 15 && !parts[0].contains(" ")) {
                    ticker = parts[0].trim();
                    name = parts[1].trim();
                }
            }
            
            map.put("id", inv.getId());
            map.put("name", name);
            map.put("tickerSymbol", ticker);
            map.put("type", inv.getType());
            map.put("amount", inv.getAmount());
            map.put("quantity", inv.getQuantity());
            map.put("date", inv.getDate());
            // Enrich with live market data if we have a ticker
            BigDecimal currentPrice = BigDecimal.ZERO;
            BigDecimal dayChange = BigDecimal.ZERO;
            BigDecimal currentValue = inv.getAmount(); // default to invested amount
            
            if (ticker != null && !ticker.trim().isEmpty() && inv.getQuantity() != null && inv.getQuantity().compareTo(BigDecimal.ZERO) > 0) {
                Map<String, BigDecimal> quote = marketDataService.getQuote(ticker);
                if (quote.containsKey("currentPrice")) {
                    currentPrice = quote.get("currentPrice");
                    dayChange = quote.getOrDefault("dayChange", BigDecimal.ZERO);
                    currentValue = currentPrice.multiply(inv.getQuantity());
                }
            } else if (inv.getQuantity() != null && inv.getQuantity().compareTo(BigDecimal.ZERO) > 0) {
                // If no ticker but we have quantity, estimate current price from amount
                currentPrice = inv.getAmount().divide(inv.getQuantity(), 2, java.math.RoundingMode.HALF_UP);
            }
            
            map.put("currentPrice", currentPrice);
            map.put("currentValue", currentValue);
            map.put("dayChange", dayChange);
            
            result.add(map);
        }
        
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<?> addInvestment(Authentication authentication, @RequestBody InvestmentRequest request) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User user = userRepository.findById(userPrincipal.getId()).orElse(null);
        
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        Investment inv = new Investment();
        inv.setUser(user);
        inv.setName(request.getName());
        inv.setTickerSymbol(request.getTickerSymbol());
        inv.setType(request.getType());
        inv.setAmount(request.getAmount());
        inv.setQuantity(request.getQuantity());
        inv.setDate(request.getDate());
        inv.setBroker(request.getBroker());
        inv.setDescription(request.getDescription());
        
        investmentRepository.save(inv);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Investment added successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Map<String, String>>> searchInvestments(@RequestParam String query) {
        List<Map<String, String>> results = marketDataService.searchSecurities(query);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/recommendations")
    public ResponseEntity<PortfolioAllocation> getInvestmentRecommendations(
            Authentication authentication,
            @RequestParam(defaultValue = "MODERATE") String riskProfile) {

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        PortfolioAllocation recommendations = investmentService.generatePortfolioRecommendation(
                userPrincipal.getId(), riskProfile.toUpperCase());

        return ResponseEntity.ok(recommendations);
    }

    @GetMapping("/capacity")
    public ResponseEntity<Map<String, Object>> getInvestmentCapacity(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        BigDecimal capacity = investmentService.calculateInvestmentCapacity(userPrincipal.getId());
        String suggestedRiskProfile = investmentService.determineRiskProfile(userPrincipal.getId());

        Map<String, Object> response = new HashMap<>();
        response.put("investmentCapacity", capacity);
        response.put("suggestedRiskProfile", suggestedRiskProfile);
        response.put("description", "Based on 20% of your monthly net income");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/risk-profiles")
    public ResponseEntity<Map<String, Object>> getRiskProfiles() {
        Map<String, Object> profiles = new HashMap<>();

        Map<String, Object> conservative = new HashMap<>();
        conservative.put("name", "Conservative");
        conservative.put("description", "Lower risk, steady returns");
        conservative.put("stocksPercentage", 40);
        conservative.put("bondsPercentage", 40);
        conservative.put("realEstatePercentage", 15);
        conservative.put("alternativesPercentage", 5);
        conservative.put("expectedReturn", "5-7%");
        conservative.put("riskLevel", "Low");

        Map<String, Object> moderate = new HashMap<>();
        moderate.put("name", "Moderate");
        moderate.put("description", "Balanced risk and return");
        moderate.put("stocksPercentage", 60);
        moderate.put("bondsPercentage", 20);
        moderate.put("realEstatePercentage", 15);
        moderate.put("alternativesPercentage", 5);
        moderate.put("expectedReturn", "7-10%");
        moderate.put("riskLevel", "Medium");

        Map<String, Object> aggressive = new HashMap<>();
        aggressive.put("name", "Aggressive");
        aggressive.put("description", "Higher risk, higher potential returns");
        aggressive.put("stocksPercentage", 80);
        aggressive.put("bondsPercentage", 10);
        aggressive.put("realEstatePercentage", 5);
        aggressive.put("alternativesPercentage", 5);
        aggressive.put("expectedReturn", "10-15%");
        aggressive.put("riskLevel", "High");

        profiles.put("CONSERVATIVE", conservative);
        profiles.put("MODERATE", moderate);
        profiles.put("AGGRESSIVE", aggressive);

        return ResponseEntity.ok(profiles);
    }
}
