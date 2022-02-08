/*
 * Copyright (C) 2021 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.goli.database.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Represents a damage-case.
 * @author andre
 */
@Document(collection = "damagecases")
public class DamageCase {
    
    @Id
    private String id;
    
    @DBRef
    private User user;
    
    private String actionCardId;
    // value of the insurance, e.g. 'Hausratsversicherung'
    private String actionCardType;
    // contains the damage case amount
    private double damageSum;
    private double damageAmountToPay;
    // how many money you will get paid by the insurance
    private double damageAmountRefund;

    public DamageCase() {
    }

    public DamageCase(String id, User user, String actionCardId, String actionCardType, double damageSum, double damageAmountToPay, double damageAmountRefund) {
        this.id = id;
        this.user = user;
        this.actionCardId = actionCardId;
        this.actionCardType = actionCardType;
        this.damageSum = damageSum;
        this.damageAmountToPay = damageAmountToPay;
        this.damageAmountRefund = damageAmountRefund;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getActionCardId() {
        return actionCardId;
    }

    public void setActionCardId(String actionCardId) {
        this.actionCardId = actionCardId;
    }

    public String getActionCardType() {
        return actionCardType;
    }

    public void setActionCardType(String actionCardType) {
        this.actionCardType = actionCardType;
    }

    public double getDamageSum() {
        return damageSum;
    }

    public void setDamageSum(double damageSum) {
        this.damageSum = damageSum;
    }

    public double getDamageAmountToPay() {
        return damageAmountToPay;
    }

    public void setDamageAmountToPay(double damageAmountToPay) {
        this.damageAmountToPay = damageAmountToPay;
    }

    public double getDamageAmountRefund() {
        return damageAmountRefund;
    }

    public void setDamageAmountRefund(double damageAmountRefund) {
        this.damageAmountRefund = damageAmountRefund;
    }

    @Override
    public String toString() {
        return "DamageCase{" + "id=" + id + ", user=" + user + ", actionCardId=" + actionCardId + ", actionCardType=" + actionCardType + ", damageSum=" + damageSum + ", damageAmountToPay=" + damageAmountToPay + ", damageAmountRefund=" + damageAmountRefund + '}';
    }
    
}
