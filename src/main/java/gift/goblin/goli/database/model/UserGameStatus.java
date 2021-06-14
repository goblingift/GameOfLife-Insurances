/*
 * Copyright (C) 2021 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.goli.database.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Contains the users game status.
 *
 * @author andre
 */
@Document(collection = "usergamestatus")
public class UserGameStatus {

    @Id
    private String id;
    private String username;
    private int level;
    private double paidForInsurances;
    private double paidForClaims;
    // this field can contain the actual action-cards type (name of Insurance) (to prevent user from picking new one)
    private String actualCardInsuranceName;
    // this field can contain the actual action-card id (to prevent user from picking new one)
    private String actualCardId;
    @DBRef
    private List<ContractedInsurance> contractedInsurances = new ArrayList<>();
    private List<String> pickedActionCards = new ArrayList<>();
    // defines if the game is over. If its true, user has to restart game
    private boolean gameOver;

    public UserGameStatus(String username, int level) {
        this.username = username;
        this.level = level;
    }

    public UserGameStatus() {
    }
    
    /**
     * Will add the given contracted Insurance to the list of insurances.
     * If there is another entry with the given id, overwrite em.
     * @param contractedInsurance the new insurance.
     */
    public void addContractedInsurance(ContractedInsurance contractedInsurance) {
        
        List<ContractedInsurance> existingContracts = contractedInsurances.stream().filter(c -> c.getInsuranceId() != contractedInsurance.getInsuranceId())
                .collect(Collectors.toList());
        existingContracts.add(contractedInsurance);
        this.contractedInsurances = existingContracts;
    }

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<ContractedInsurance> getContractedInsurances() {
        return contractedInsurances;
    }

    public void setContractedInsurances(List<ContractedInsurance> contractedInsurances) {
        this.contractedInsurances = contractedInsurances;
    }

    public String getActualCardInsuranceName() {
        return actualCardInsuranceName;
    }

    public void setActualCardInsuranceName(String actualCardInsuranceId) {
        this.actualCardInsuranceName = actualCardInsuranceId;
    }

    public String getActualCardId() {
        return actualCardId;
    }

    public void setActualCardId(String actualCardId) {
        this.actualCardId = actualCardId;
    }

    public List<String> getPickedActionCards() {
        return pickedActionCards;
    }

    public void setPickedActionCards(List<String> pickedActionCards) {
        this.pickedActionCards = pickedActionCards;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.id);
        hash = 29 * hash + Objects.hashCode(this.username);
        hash = 29 * hash + this.level;
        hash = 29 * hash + (int) (Double.doubleToLongBits(this.paidForInsurances) ^ (Double.doubleToLongBits(this.paidForInsurances) >>> 32));
        hash = 29 * hash + (int) (Double.doubleToLongBits(this.paidForClaims) ^ (Double.doubleToLongBits(this.paidForClaims) >>> 32));
        hash = 29 * hash + Objects.hashCode(this.contractedInsurances);
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
        final UserGameStatus other = (UserGameStatus) obj;
        if (this.level != other.level) {
            return false;
        }
        if (Double.doubleToLongBits(this.paidForInsurances) != Double.doubleToLongBits(other.paidForInsurances)) {
            return false;
        }
        if (Double.doubleToLongBits(this.paidForClaims) != Double.doubleToLongBits(other.paidForClaims)) {
            return false;
        }
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.username, other.username)) {
            return false;
        }
        if (!Objects.equals(this.contractedInsurances, other.contractedInsurances)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "UserGameStatus{" + "id=" + id + ", username=" + username + ", level=" + level + ", paidForInsurances=" + paidForInsurances + ", paidForClaims=" + paidForClaims + ", actualCardInsuranceName=" + actualCardInsuranceName + ", actualCardId=" + actualCardId + ", contractedInsurances=" + contractedInsurances + ", pickedActionCards=" + pickedActionCards + ", gameOver=" + gameOver + '}';
    }

}
