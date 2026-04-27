package com.finance.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class InvestmentRequest {
    private String name;
    private String tickerSymbol;
    private String type;
    private BigDecimal amount;
    private BigDecimal quantity;
    private LocalDate date;
    private String broker;
    private String description;

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getTickerSymbol() { return tickerSymbol; }
    public void setTickerSymbol(String tickerSymbol) { this.tickerSymbol = tickerSymbol; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
    
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    
    public String getBroker() { return broker; }
    public void setBroker(String broker) { this.broker = broker; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
