/*
 * Copyright (C) 2021 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.goli.dto;

/**
 * Contains the users game status.
 * @author andre
 */
public class UserGameStatus {
    
    private String username;
    private int level;
    private double paidForInsurances;
    private double paidForClaims;

    public UserGameStatus(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public double getPaidForInsurances() {
        return paidForInsurances;
    }

    public void setPaidForInsurances(double paidForInsurances) {
        this.paidForInsurances = paidForInsurances;
    }

    public double getPaidForClaims() {
        return paidForClaims;
    }

    public void setPaidForClaims(double paidForClaims) {
        this.paidForClaims = paidForClaims;
    }
    
    
    
}
