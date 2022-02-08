/*
 * Copyright (C) 2021 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.goli.dto;

/**
 * This class contains all fields which are required to render an action-card,
 * which were picked by a player.
 *
 * @author andre
 */
public class ActionCardText {

    /**
     * Contains the insurance-type as readable text, like "KFZ-Versicherung".
     */
    private String insuranceType;

    /**
     * Contains the action-card id.
     */
    private String actionCardId;

    /**
     * Contains the damage-case description, as it was written by the card
     * author.
     */
    private String damageCaseDescription;

    /**
     * Contains additional information about this damage-case, e.g. if your
     * insurance will pay the damage or not.
     */
    private String damageCaseAdditionalDescription;

    /**
     * Contains the original damage amount, as defined by card author.
     */
    private double damageAmount;

    /**
     * The amount of money, which the player has to pay. Will be zero, if he has
     * a good insurance.
     */
    private double damageAmountToPay;
    
    /**
     * The amount of money, which will get paid by the insurance.
     */
    private double savedMoney;
    
    /**
     * This field can be filled with a specific property key, to display
     * different title for the damage amount sum label
     */
    private String differentTextDamageAmountSum;

    /**
     * This field can be filled with a specific property key, to display
     * different title for the damage amount sum label
     */
    private String differentTextDamageAmount;

    
    public ActionCardText() {
    }

    public ActionCardText(String insuranceType, String actionCardId, String damageCaseDescription, String damageCaseAdditionalDescription, double damageAmount, double damageAmountToPay, double savedMoney) {
        this.insuranceType = insuranceType;
        this.actionCardId = actionCardId;
        this.damageCaseDescription = damageCaseDescription;
        this.damageCaseAdditionalDescription = damageCaseAdditionalDescription;
        this.damageAmount = damageAmount;
        this.damageAmountToPay = damageAmountToPay;
        this.savedMoney = savedMoney;
    }

    public String getInsuranceType() {
        return insuranceType;
    }

    public void setInsuranceType(String insuranceType) {
        this.insuranceType = insuranceType;
    }

    public String getActionCardId() {
        return actionCardId;
    }

    public void setActionCardId(String actionCardId) {
        this.actionCardId = actionCardId;
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

    public String getDifferentTextDamageAmountSum() {
        return differentTextDamageAmountSum;
    }

    public void setDifferentTextDamageAmountSum(String differentTextDamageAmountSum) {
        this.differentTextDamageAmountSum = differentTextDamageAmountSum;
    }

    public String getDifferentTextDamageAmount() {
        return differentTextDamageAmount;
    }

    public void setDifferentTextDamageAmount(String differentTextDamageAmount) {
        this.differentTextDamageAmount = differentTextDamageAmount;
    }

    public double getSavedMoney() {
        return savedMoney;
    }

    public void setSavedMoney(double savedMoney) {
        this.savedMoney = savedMoney;
    }

    @Override
    public String toString() {
        return "ActionCardText{" + "insuranceType=" + insuranceType + ", actionCardId=" + actionCardId + ", damageCaseDescription=" + damageCaseDescription + ", damageCaseAdditionalDescription=" + damageCaseAdditionalDescription + ", damageAmount=" + damageAmount + ", damageAmountToPay=" + damageAmountToPay + ", savedMoney=" + savedMoney + ", differentTextDamageAmountSum=" + differentTextDamageAmountSum + ", differentTextDamageAmount=" + differentTextDamageAmount + '}';
    }
    
}
