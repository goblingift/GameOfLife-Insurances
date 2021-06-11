/*
 * Copyright (C) 2021 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.goli.controller;

import gift.goblin.goli.WebSecurityConfig;
import gift.goblin.goli.database.model.UserGameStatus;
import gift.goblin.goli.dto.ActionCardText;
import gift.goblin.goli.dto.DecisionAnswer;
import gift.goblin.goli.enumerations.Insurance;
import gift.goblin.goli.enumerations.Level;
import gift.goblin.goli.enumerations.LevelType;
import gift.goblin.goli.security.service.GameCardService;
import java.util.Optional;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author andre
 */
@Controller
@RequestMapping("/game")
public class GameController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private BuildProperties buildProperties;

    @Autowired
    private GameCardService gameCardService;

    @GetMapping
    public String renderGameBoard(HttpSession session, Model model) {
        logger.info("User opened game board page.");
        
        UserGameStatus userGameStatus = gameCardService.getUserGameStatus(getUsernameFromSession(session));
        Optional<Level> optLevel = Level.findByLevel(userGameStatus.getLevel());
        
        if (optLevel.isPresent()) {
            logger.info("Adding levelType to model: {}", optLevel.get().getLevelType().toString());
            model.addAttribute("levelType", optLevel.get().getLevelType().toString());
        } else {
            logger.warn("Couldnt resolve the level: {}", userGameStatus.getLevel());
        }
        
        model.addAttribute("userGameStatus", userGameStatus);
        model.addAttribute("build_artifact", buildProperties.getArtifact());
        model.addAttribute("build_version", buildProperties.getVersion());
        return "game_board";
    }

    private String getUsernameFromSession(HttpSession session) {
        return (String) session.getAttribute(WebSecurityConfig.SESSION_FIELD_USERNAME);
    }

    @PostMapping("/make-decision")
    @ResponseBody
    public void submitDecision(DecisionAnswer decisionAnswer, HttpSession session, Model model) {
        
        logger.info("Called submitDecision with data: {}", decisionAnswer);
        UserGameStatus userGameStatus = gameCardService.getUserGameStatus(getUsernameFromSession(session));
        
        Optional<Level> optLevel = Level.findByLevel(decisionAnswer.getLevel());
        if (optLevel.isEmpty()) {
            logger.warn("User tried to make decision for unknown level: {}", decisionAnswer.getLevel());
            return;
        }
        // If user is currently in a level where a decision can be made, handle it
        if (optLevel.get().getLevelType() == LevelType.DECISION || optLevel.get().getLevelType() == LevelType.INSURANCE) {
            boolean handledUserDecision = gameCardService.handleUserDecision(getUsernameFromSession(session), decisionAnswer.getLevel(), decisionAnswer.getAnswer());
        } else if (optLevel.get().getLevelType() == LevelType.ACTION) {
            gameCardService.handleActionCard(userGameStatus);
        }
        
        // Set player to next level
        gameCardService.moveUserToNextLevel(getUsernameFromSession(session), decisionAnswer.getLevel());
    }
    
    
    @GetMapping("/new-game")
    @ResponseBody
    public void startNewGame(HttpSession session, Model model) {
        logger.info("User called /new-game, will start new game in level 1.");
        
        gameCardService.startNewGame(getUsernameFromSession(session));
    }
    
    
    @GetMapping("/get-dialog")
    public String getDialogContent(HttpSession session, Model model) {
        
        // User picked new card- so update the level of the user
        UserGameStatus userGameStatus = gameCardService.getUserGameStatus(getUsernameFromSession(session));
        model.addAttribute("userGameStatus", userGameStatus);
        
        // check what kind of action will be next
        Optional<Level> optLevel = Level.findByLevel(userGameStatus.getLevel());
        if (optLevel.isPresent()) {
            LevelType nextLevelType = optLevel.get().getLevelType();
            switch (nextLevelType) {
                case INSURANCE:
                    userGameStatus = gameCardService.addInsuranceToUserGameStatus(userGameStatus, optLevel.get().getLevel());
                    model.addAttribute("userGameStatus", userGameStatus);
                    return "/decision/triple_options_decision :: replace_fragment";
                case DECISION:
                    return "/decision/two_options_decision :: replace_fragment";
                case INFO:
                    return "/decision/simple_info :: replace_fragment";
                case ACTION:
                    ActionCardText actionCardText = gameCardService.getNewRandomActionCard(userGameStatus);
                    logger.info("Will add new actionCardText to model: {}", actionCardText);
                    model.addAttribute("actionCardText", actionCardText);
                    return "/decision/action_card :: replace_fragment";
                default:
                    throw new AssertionError();
            }
        } else {
            logger.warn("Couldnt resolve the level: {}", userGameStatus.getLevel());
        }
        
        return null;
    }
    
    

}
