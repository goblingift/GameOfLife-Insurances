/*
 * Copyright (C) 2021 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.goli.dto;

/**
 *
 * @author andre
 */
public class GameStatus {
    
    private boolean createCards;
    private boolean gameEnabled;

    public GameStatus(boolean createCards, boolean gameEnabled) {
        this.createCards = createCards;
        this.gameEnabled = gameEnabled;
    }

    public boolean isCreateCards() {
        return createCards;
    }

    public void setCreateCards(boolean createCards) {
        this.createCards = createCards;
    }

    public boolean isGameEnabled() {
        return gameEnabled;
    }

    public void setGameEnabled(boolean gameEnabled) {
        this.gameEnabled = gameEnabled;
    }
    
    
    
}
