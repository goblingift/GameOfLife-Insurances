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
@Document(collection = "actioncards_liability_insurance")
public class LiabilityInsuranceActionCard {

    @Id
    private String id;
    private int insuranceId;
    private String text;
    private double damageAmount;

    public LiabilityInsuranceActionCard() {
    }

    public LiabilityInsuranceActionCard(String id, int insuranceId, String text, double damageAmount) {
        this.id = id;
        this.insuranceId = insuranceId;
        this.text = text;
        this.damageAmount = damageAmount;
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

    @Override
    public String toString() {
        return "LiabilityInsuranceActionCard{" + "id=" + id + ", insuranceId=" + insuranceId + ", text=" + text + ", damageAmount=" + damageAmount + '}';
    }
    
}
