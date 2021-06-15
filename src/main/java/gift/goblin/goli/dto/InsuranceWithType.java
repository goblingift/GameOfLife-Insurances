/*
 * Copyright (C) 2021 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.goli.dto;

import gift.goblin.goli.enumerations.Insurance;

/**
 * Small dto, which contains an action-card ID and its type.
 * @author andre
 */
public class InsuranceWithType {
    
    private String id;
    private Insurance insurance;

    public InsuranceWithType(String id, Insurance insurance) {
        this.id = id;
        this.insurance = insurance;
    }

    public String getId() {
        return id;
    }

    public Insurance getInsurance() {
        return insurance;
    }

    @Override
    public String toString() {
        return "InsuranceWithType{" + "id=" + id + ", insurance=" + insurance + '}';
    }
    
}
