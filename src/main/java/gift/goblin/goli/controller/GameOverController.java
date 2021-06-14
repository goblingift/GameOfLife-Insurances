/*
 * Copyright (C) 2021 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.goli.controller;

import gift.goblin.goli.database.model.UserGameStatus;
import gift.goblin.goli.dto.GameOverSummary;
import gift.goblin.goli.enumerations.Level;
import gift.goblin.goli.service.GameCardService;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller to render the game-over page.
 *
 * @author andre
 */
@Controller
@RequestMapping("/game-over")
public class GameOverController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private GameCardService gameCardService;

    @GetMapping()
    public String renderGameSummary(HttpSession session, Model model) {
        
        String username = gameCardService.getUsernameFromSession(session);
        UserGameStatus userGameStatus = gameCardService.getUserGameStatus(username);
        
        if (userGameStatus.getLevel() != Level.getSIZE()) {
            logger.warn("User {} tried to open game-over summary, but hasnt reached level-end! Redirect to game...",
                    username);
            return "redirect:/game";
        }
        
        GameOverSummary gameOverSummary = gameCardService.generateGameSummary(username);
        model.addAttribute("summary", gameOverSummary);
        
        return "game_over";
    }
    

}
