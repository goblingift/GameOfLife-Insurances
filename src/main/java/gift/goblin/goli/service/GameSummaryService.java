/*
 * Copyright (C) 2021 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.goli.service;

import gift.goblin.goli.database.model.UserGameStatus;
import gift.goblin.goli.dto.GameOverSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Contains logic to generate summary statistics of the game.
 *
 * @author andre
 */
@Service
public class GameSummaryService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    GameCardService gameCardService;
    
    
    public GameOverSummary generateGameSummary(String username) {

        UserGameStatus userGameStatus = gameCardService.getUserGameStatus(username);
        
        GameOverSummary gameOverSummary = new GameOverSummary();
        gameOverSummary.setPaidCostsSum(userGameStatus.getPaidForInsurances());
        gameOverSummary.setPaidDamageCostsSum(userGameStatus.getPaidForClaims());
        
        return gameOverSummary;
    }
    
    
    

}
