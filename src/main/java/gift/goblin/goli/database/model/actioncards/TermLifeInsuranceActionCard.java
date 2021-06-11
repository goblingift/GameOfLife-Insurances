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
@Document(collection = "actioncards_termlife_insurance")
public class TermLifeInsuranceActionCard {
    
    @Id
    private String id;
    private int insuranceId;
    private String text;

    public TermLifeInsuranceActionCard() {
    }

    public TermLifeInsuranceActionCard(String id, int insuranceId, String text) {
        this.id = id;
        this.insuranceId = insuranceId;
        this.text = text;
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

    @Override
    public String toString() {
        return "TermLifeInsuranceActionCard{" + "id=" + id + ", insuranceId=" + insuranceId + ", text=" + text + '}';
    }

}
