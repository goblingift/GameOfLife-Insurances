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
@Document(collection = "actioncards_car_insurance")
public class CarInsuranceActionCard {
    
    @Id
    private String id;
    private int insuranceId;
    private String text;
    private double damageAmount;
    private boolean coveredByHaftpflicht;
    private boolean coveredByTeilkasko;
    private boolean coveredByVollkasko;

    public CarInsuranceActionCard() {
    }

    public CarInsuranceActionCard(String id, int insuranceId, String text, double damageAmount, boolean coveredByHaftpflicht, boolean coveredByTeilkasko, boolean coveredByVollkasko) {
        this.id = id;
        this.insuranceId = insuranceId;
        this.text = text;
        this.damageAmount = damageAmount;
        this.coveredByHaftpflicht = coveredByHaftpflicht;
        this.coveredByTeilkasko = coveredByTeilkasko;
        this.coveredByVollkasko = coveredByVollkasko;
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

    public boolean isCoveredByHaftpflicht() {
        return coveredByHaftpflicht;
    }

    public void setCoveredByHaftpflicht(boolean coveredByHaftpflicht) {
        this.coveredByHaftpflicht = coveredByHaftpflicht;
    }

    public boolean isCoveredByTeilkasko() {
        return coveredByTeilkasko;
    }

    public void setCoveredByTeilkasko(boolean coveredByTeilkasko) {
        this.coveredByTeilkasko = coveredByTeilkasko;
    }

    public boolean isCoveredByVollkasko() {
        return coveredByVollkasko;
    }

    public void setCoveredByVollkasko(boolean coveredByVollkasko) {
        this.coveredByVollkasko = coveredByVollkasko;
    }

    @Override
    public String toString() {
        return "CarInsuranceActionCard{" + "id=" + id + ", insuranceId=" + insuranceId + ", text=" + text + ", damageAmount=" + damageAmount + ", coveredByHaftpflicht=" + coveredByHaftpflicht + ", coveredByTeilkasko=" + coveredByTeilkasko + ", coveredByVollkasko=" + coveredByVollkasko + '}';
    }

}
