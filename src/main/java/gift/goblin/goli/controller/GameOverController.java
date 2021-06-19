/*
 * Copyright (C) 2021 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.goli.controller;

import gift.goblin.goli.WebSecurityConfig;
import gift.goblin.goli.database.model.UserGameStatus;
import gift.goblin.goli.dto.GameOverSummary;
import gift.goblin.goli.enumerations.Level;
import gift.goblin.goli.service.GameCardService;
import gift.goblin.goli.service.GameSummaryService;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
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

    @Autowired
    private GameSummaryService gameSummaryService;

    @GetMapping()
    public String renderGameSummary(HttpSession session, Model model, Authentication authentication) {

        String username = gameCardService.getUsernameFromSession(session);
        UserGameStatus userGameStatus = gameCardService.getUserGameStatus(username);

        if (userGameStatus.getLevel() != Level.getSIZE() && !isUserAdmin(authentication)) {
            logger.warn("User {} tried to open game-over summary, but hasnt reached level-end! Redirect to game...",
                    username);
            return "redirect:/game";
        }

        List<GameOverSummary> gameSummaryAllPlayers = gameSummaryService.generateGameSummaryAllPlayers();
        GameOverSummary gameOverSummary = gameSummaryService.generateGameSummary(username);
        logger.info("Adding gameOverSummary to model: {}", gameOverSummary);
        model.addAttribute("isAdmin", isUserAdmin(authentication));
        model.addAttribute("summary", gameOverSummary);
        model.addAttribute("allPlayerSummary", gameSummaryAllPlayers);
        return "game_over";
    }

    private boolean isUserAdmin(Authentication authentication) {
        if (authentication != null && authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equalsIgnoreCase(WebSecurityConfig.ROLE_ADMIN))) {
            return true;
        } else {
            return false;
        }
    }
}
