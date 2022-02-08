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
@Document(collection = "actioncards_legalprotection_insurance")
public class LegalProtectionInsuranceActionCard {
    
    @Id
    private String id;
    private int insuranceId;
    private String text;
    private double damageAmount;
    private boolean coveredByPrivateInsurance;
    private boolean coveredByTrafficInsurance;
    private boolean coveredByJobInsurance;

    public LegalProtectionInsuranceActionCard() {
    }

    public LegalProtectionInsuranceActionCard(String id, int insuranceId, String text, double damageAmount, boolean coveredByPrivateInsurance, boolean coveredByTrafficInsurance, boolean coveredByJobInsurance) {
        this.id = id;
        this.insuranceId = insuranceId;
        this.text = text;
        this.damageAmount = damageAmount;
        this.coveredByPrivateInsurance = coveredByPrivateInsurance;
        this.coveredByTrafficInsurance = coveredByTrafficInsurance;
        this.coveredByJobInsurance = coveredByJobInsurance;
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

    public boolean isCoveredByPrivateInsurance() {
        return coveredByPrivateInsurance;
    }

    public void setCoveredByPrivateInsurance(boolean coveredByPrivateInsurance) {
        this.coveredByPrivateInsurance = coveredByPrivateInsurance;
    }

    public boolean isCoveredByTrafficInsurance() {
        return coveredByTrafficInsurance;
    }

    public void setCoveredByTrafficInsurance(boolean coveredByTrafficInsurance) {
        this.coveredByTrafficInsurance = coveredByTrafficInsurance;
    }

    public boolean isCoveredByJobInsurance() {
        return coveredByJobInsurance;
    }

    public void setCoveredByJobInsurance(boolean coveredByJobInsurance) {
        this.coveredByJobInsurance = coveredByJobInsurance;
    }

    @Override
    public String toString() {
        return "LegalProtectionInsuranceActionCard{" + "id=" + id + ", insuranceId=" + insuranceId + ", text=" + text + ", damageAmount=" + damageAmount + ", coveredByPrivateInsurance=" + coveredByPrivateInsurance + ", coveredByTrafficInsurance=" + coveredByTrafficInsurance + ", coveredByJobInsurance=" + coveredByJobInsurance + '}';
    }
    
}
