/*
 * Copyright (C) 2021 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.goli.dto;

/**
 *
 * @author andre
 */
public class InsuranceSelection {
    private int insuranceId;

    public InsuranceSelection() {
    }

    public InsuranceSelection(int insuranceId) {
        this.insuranceId = insuranceId;
    }

    public int getInsuranceId() {
        return insuranceId;
    }

    public void setInsuranceId(int insuranceId) {
        this.insuranceId = insuranceId;
    }

    @Override
    public String toString() {
        return "InsuranceSelection{" + "insuranceId=" + insuranceId + '}';
    }
    
}
