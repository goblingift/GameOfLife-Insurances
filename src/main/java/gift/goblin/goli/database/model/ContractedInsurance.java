/*
 * Copyright (C) 2021 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.goli.database.model;

import java.util.Objects;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 *
 * @author andre
 */
@Document(collection = "insurances")
public class ContractedInsurance {
    
    @Id
    private String id;
    
    @DBRef
    private User user;

    private int insuranceId;

    private double yearlyCost;

    // Contains the selected value of the user
    private int selectedChoice;

    public ContractedInsurance() {
    }

    public ContractedInsurance(String id, User user, int insuranceId, double yearlyCost, int selectedChoice) {
        this.id = id;
        this.user = user;
        this.insuranceId = insuranceId;
        this.yearlyCost = yearlyCost;
        this.selectedChoice = selectedChoice;
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

    public double getYearlyCost() {
        return yearlyCost;
    }

    public void setYearlyCost(double yearlyCost) {
        this.yearlyCost = yearlyCost;
    }

    public int getSelectedChoice() {
        return selectedChoice;
    }

    public void setSelectedChoice(int selectedChoice) {
        this.selectedChoice = selectedChoice;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + Objects.hashCode(this.id);
        hash = 17 * hash + this.insuranceId;
        hash = 17 * hash + (int) (Double.doubleToLongBits(this.yearlyCost) ^ (Double.doubleToLongBits(this.yearlyCost) >>> 32));
        hash = 17 * hash + this.selectedChoice;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ContractedInsurance other = (ContractedInsurance) obj;
        if (this.insuranceId != other.insuranceId) {
            return false;
        }
        if (Double.doubleToLongBits(this.yearlyCost) != Double.doubleToLongBits(other.yearlyCost)) {
            return false;
        }
        if (this.selectedChoice != other.selectedChoice) {
            return false;
        }
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.user, other.user)) {
            return false;
        }
        return true;
    }

    

    @Override
    public String toString() {
        return "ContractedInsurance{" + "id=" + id + ", insuranceId=" + insuranceId + ", yearlyCost=" + yearlyCost + ", selectedChoice=" + selectedChoice + '}';
    }

}
