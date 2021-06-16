/*
 * Copyright (C) 2021 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.goli.database.model.actioncards;

import java.util.Objects;
import org.springframework.data.annotation.Id;

/**
 * This class represents a positive action-card of predefined situations which
 * could occur in everyones life.
 *
 * @author andre
 */
public class ActionCard {

    @Id
    private String id;
    private String title;
    private String text;
    /**
     * This is the positive amount, the player will get through this card.
     */
    private double amount;

    public ActionCard(String id, String title, String text, double amount) {
        this.id = id;
        this.title = title;
        this.text = text;
        this.amount = amount;
    }

    public ActionCard() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + Objects.hashCode(this.id);
        hash = 47 * hash + Objects.hashCode(this.title);
        hash = 47 * hash + Objects.hashCode(this.text);
        hash = 47 * hash + (int) (Double.doubleToLongBits(this.amount) ^ (Double.doubleToLongBits(this.amount) >>> 32));
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
        final ActionCard other = (ActionCard) obj;
        if (Double.doubleToLongBits(this.amount) != Double.doubleToLongBits(other.amount)) {
            return false;
        }
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.title, other.title)) {
            return false;
        }
        if (!Objects.equals(this.text, other.text)) {
            return false;
        }
        return true;
    }

    
    
    @Override
    public String toString() {
        return "ActionCard{" + "id=" + id + ", title=" + title + ", text=" + text + ", amount=" + amount + '}';
    }
    
    
    
}
