/*
 * Copyright (C) 2021 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.goli.database.model.actioncards;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 *
 * @author andre
 */
@Document(collection = "actioncards_smartphone_insurance")
public class SmartphoneInsuranceActionCard {
    
    @Id
    private String id;
    private int insuranceId;
    private String text;
    private double damageAmount;
    // Defines if the damage-case is only covered if user selected additional insurance package for stolen devices
    private boolean coveredByAddition;

    public SmartphoneInsuranceActionCard() {
    }

    public SmartphoneInsuranceActionCard(String id, int insuranceId, String text, double damageAmount, boolean coveredByAddition) {
        this.id = id;
        this.insuranceId = insuranceId;
        this.text = text;
        this.damageAmount = damageAmount;
        this.coveredByAddition = coveredByAddition;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getInsuranceId() {
        return insuranceId;
    }

    public void setInsuranceId(int insuranceId) {
        this.insuranceId = insuranceId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public double getDamageAmount() {
        return damageAmount;
    }

    public void setDamageAmount(double damageAmount) {
        this.damageAmount = damageAmount;
    }

    public boolean isCoveredByAddition() {
        return coveredByAddition;
    }

    public void setCoveredByAddition(boolean coveredByAddition) {
        this.coveredByAddition = coveredByAddition;
    }

    @Override
    public String toString() {
        return "SmartphoneInsuranceActionCard{" + "id=" + id + ", insuranceId=" + insuranceId + ", text=" + text + ", damageAmount=" + damageAmount + ", coveredByAddition=" + coveredByAddition + '}';
    }
    
}
