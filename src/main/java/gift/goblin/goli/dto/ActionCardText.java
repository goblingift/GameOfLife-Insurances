/*
 * Copyright (C) 2021 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.goli.dto;

/**
 * This class contains all fields which are required to render an action-card,
 * which were picked by a player.
 * @author andre
 */
public class ActionCardText {
    
    /**
     * Contains the insurance-type as readable text, like "KFZ-Versicherung".
     */
    private String insuranceType;
    
    /**
     * Contains the damage-case description, as it was written by the card author.
     */
    private String damageCaseDescription;
    
    /**
     * Contains additional information about this damage-case, e.g. if your insurance
     * will pay the damage or not.
     */
    private String damageCaseAdditionalDescription;
    
    /**
     * Contains the original damage amount, as defined by card author.
     */
    private double damageAmount;
    
    /**
     * The amount of money, which the player has to pay. Will be zero, if he has a good insurance.
     */
    private double damageAmountToPay;

    public ActionCardText() {
    }

    public ActionCardText(String insuranceType, String damageCaseDescription, String damageCaseAdditionalDescription, double damageAmount, double damageAmountToPay) {
        this.insuranceType = insuranceType;
        this.damageCaseDescription = damageCaseDescription;
        this.damageCaseAdditionalDescription = damageCaseAdditionalDescription;
        this.damageAmount = damageAmount;
        this.damageAmountToPay = damageAmountToPay;
    }

    public String getInsuranceType() {
        return insuranceType;
    }

    public void setInsuranceType(String insuranceType) {
        this.insuranceType = insuranceType;
    }

    public String getDamageCaseDescription() {
        return damageCaseDescription;
    }

    public void setDamageCaseDescription(String damageCaseDescription) {
        this.damageCaseDescription = damageCaseDescription;
    }

    public String getDamageCaseAdditionalDescription() {
        return damageCaseAdditionalDescription;
    }

    public void setDamageCaseAdditionalDescription(String damageCaseAdditionalDescription) {
        this.damageCaseAdditionalDescription = damageCaseAdditionalDescription;
    }

    public double getDamageAmount() {
        return damageAmount;
    }

    public void setDamageAmount(double damageAmount) {
        this.damageAmount = damageAmount;
    }

    public double getDamageAmountToPay() {
        return damageAmountToPay;
    }

    public void setDamageAmountToPay(double damageAmountToPay) {
        this.damageAmountToPay = damageAmountToPay;
    }

    @Override
    public String toString() {
        return "ActionCardText{" + "insuranceType=" + insuranceType + ", damageCaseDescription=" + damageCaseDescription + ", damageCaseAdditionalDescription=" + damageCaseAdditionalDescription + ", damageAmount=" + damageAmount + ", damageAmountToPay=" + damageAmountToPay + '}';
    }
    
}
