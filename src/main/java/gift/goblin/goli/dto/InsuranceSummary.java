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
    // years of membership
    private int years;
    // paid costs for the membership of that insurance
    private double paidCosts;
    // how many money the insurance paid for damages
    private double savedMoney;
    // how many money you have spent for damage-cases, which should be covered by that insurance
    private double paidDamageCosts;
    // Contains the selected package of that insurance, e.g. 'Haftpflicht ohne SB (Kostet 70€ im Jahr)'
    private String selectedInsurance;
    // how many damage cases you had in your life
    private int damageCases;
    
    public InsuranceSummary() {
    }

    public InsuranceSummary(String insuranceName) {
        this.insuranceName = insuranceName;
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

    public String getSelectedInsurance() {
        return selectedInsurance;
    }

    public void setSelectedInsurance(String selectedInsurance) {
        this.selectedInsurance = selectedInsurance;
    }

    public int getDamageCases() {
        return damageCases;
    }

    public void setDamageCases(int damageCases) {
        this.damageCases = damageCases;
    }

    @Override
    public String toString() {
        return "InsuranceSummary{" + "insuranceName=" + insuranceName + ", agreed=" + agreed + ", years=" + years + ", paidCosts=" + paidCosts + ", savedMoney=" + savedMoney + ", paidDamageCosts=" + paidDamageCosts + ", selectedInsurance=" + selectedInsurance + ", damageCases=" + damageCases + '}';
    }

}
