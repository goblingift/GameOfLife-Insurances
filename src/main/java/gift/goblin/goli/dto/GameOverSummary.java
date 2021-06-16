/*
 * Copyright (C) 2021 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.goli.dto;

import java.util.List;

/**
 *
 * @author andre
 */
public class GameOverSummary {
    
    private String username;
    private double paidCostsSum;
    private double savedMoneySum;
    private double paidDamageCostsSum;
    private List<InsuranceSummary> insuranceSummaries;

    public GameOverSummary() {
    }

    public GameOverSummary(double paidCostsSum, double savedMoneySum, double paidDamageCostsSum, List<InsuranceSummary> insuranceSummaries) {
        this.paidCostsSum = paidCostsSum;
        this.savedMoneySum = savedMoneySum;
        this.paidDamageCostsSum = paidDamageCostsSum;
        this.insuranceSummaries = insuranceSummaries;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public double getPaidCostsSum() {
        return paidCostsSum;
    }

    public void setPaidCostsSum(double paidCostsSum) {
        this.paidCostsSum = paidCostsSum;
    }

    public double getSavedMoneySum() {
        return savedMoneySum;
    }

    public void setSavedMoneySum(double savedMoneySum) {
        this.savedMoneySum = savedMoneySum;
    }

    public double getPaidDamageCostsSum() {
        return paidDamageCostsSum;
    }

    public void setPaidDamageCostsSum(double paidDamageCostsSum) {
        this.paidDamageCostsSum = paidDamageCostsSum;
    }

    public List<InsuranceSummary> getInsuranceSummaries() {
        return insuranceSummaries;
    }

    public void setInsuranceSummaries(List<InsuranceSummary> insuranceSummaries) {
        this.insuranceSummaries = insuranceSummaries;
    }

    @Override
    public String toString() {
        return "GameOverSummary{" + "paidCostsSum=" + paidCostsSum + ", savedMoneySum=" + savedMoneySum + ", paidDamageCostsSum=" + paidDamageCostsSum + ", insuranceSummaries=" + insuranceSummaries + '}';
    }
    
}
