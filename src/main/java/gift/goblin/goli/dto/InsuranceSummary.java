/*
 * Copyright (C) 2021 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.goli.dto;

/**
 * This class defines fields for the summary of a specific insurance.
 * @author andre
 */
public class InsuranceSummary {
    
    private String insuranceName;
    // true if user bought that insurance- false if not
    private boolean agreed;
    private int years;
    private double paidCosts;
    private double savedMoney;
    private double paidDamageCosts;

    public InsuranceSummary() {
    }

    public InsuranceSummary(String insuranceName, boolean agreed, int years, double paidCosts, double savedMoney, double paidDamageCosts) {
        this.insuranceName = insuranceName;
        this.agreed = agreed;
        this.years = years;
        this.paidCosts = paidCosts;
        this.savedMoney = savedMoney;
        this.paidDamageCosts = paidDamageCosts;
    }

    public String getInsuranceName() {
        return insuranceName;
    }

    public void setInsuranceName(String insuranceName) {
        this.insuranceName = insuranceName;
    }

    public boolean isAgreed() {
        return agreed;
    }

    public void setAgreed(boolean agreed) {
        this.agreed = agreed;
    }

    public int getYears() {
        return years;
    }

    public void setYears(int years) {
        this.years = years;
    }

    public double getPaidCosts() {
        return paidCosts;
    }

    public void setPaidCosts(double paidCosts) {
        this.paidCosts = paidCosts;
    }

    public double getSavedMoney() {
        return savedMoney;
    }

    public void setSavedMoney(double savedMoney) {
        this.savedMoney = savedMoney;
    }

    public double getPaidDamageCosts() {
        return paidDamageCosts;
    }

    public void setPaidDamageCosts(double paidDamageCosts) {
        this.paidDamageCosts = paidDamageCosts;
    }

    @Override
    public String toString() {
        return "InsuranceSummary{" + "insuranceName=" + insuranceName + ", agreed=" + agreed + ", years=" + years + ", paidCosts=" + paidCosts + ", savedMoney=" + savedMoney + ", paidDamageCosts=" + paidDamageCosts + '}';
    }
    
}
