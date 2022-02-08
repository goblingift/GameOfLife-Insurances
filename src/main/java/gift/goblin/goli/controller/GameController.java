/*
 * Copyright (C) 2021 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.goli.controller;

import gift.goblin.goli.WebSecurityConfig;
import gift.goblin.goli.database.model.UserGameStatus;
import gift.goblin.goli.database.model.actioncards.ActionCard;
import gift.goblin.goli.database.repository.ActionCardRepository;
import gift.goblin.goli.database.repository.UserGameStatusRepository;
import gift.goblin.goli.dto.ActionCardText;
import gift.goblin.goli.dto.DecisionAnswer;
import gift.goblin.goli.enumerations.Insurance;
import gift.goblin.goli.enumerations.Level;
import gift.goblin.goli.enumerations.LevelType;
import gift.goblin.goli.service.GameCardService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
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
    
    @Autowired
    private ActionCardRepository actionCardRepository;
    
    @Autowired
    private UserGameStatusRepository userGameStatusRepository;

    @GetMapping
    public String renderGameBoard(HttpSession session, Model model) {
        logger.info("User opened game board page.");
        
        String username = gameCardService.getUsernameFromSession(session);
        UserGameStatus userGameStatus = gameCardService.getUserGameStatus(username);
        
        if (userGameStatus.isGameOver()) {
            logger.warn("User {} is game-over, redirect em again to the result-page.", username);
            return "redirect:/game-over";
        }
        
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


    @PostMapping("/make-decision")
    @ResponseBody
    public boolean submitDecision(DecisionAnswer decisionAnswer, HttpSession session, Model model) {
        
        logger.info("Called submitDecision with data: {}", decisionAnswer);
        UserGameStatus userGameStatus = gameCardService.getUserGameStatus(gameCardService.getUsernameFromSession(session));
        boolean gameContinues = true;
        
        Optional<Level> optLevel = Level.findByLevel(decisionAnswer.getLevel());
        if (optLevel.isEmpty()) {
            logger.warn("User tried to make decision for unknown level: {}", decisionAnswer.getLevel());
            return gameContinues;
        }
        
        // If user just approves a neutral actioncard- do it
        if (decisionAnswer.getAnswer() == 999) {
            logger.info("User approved neutral message. Continue game.");
        } else if (optLevel.get().getLevelType() == LevelType.DECISION || optLevel.get().getLevelType() == LevelType.INSURANCE) {
            // If user is currently in a level where a decision can be made, handle it
            gameContinues = gameCardService.handleUserDecision(gameCardService.getUsernameFromSession(session), decisionAnswer.getLevel(), decisionAnswer.getAnswer());
        
        } else if (optLevel.get().getLevelType() == LevelType.ACTION) {
            gameContinues = gameCardService.handleActionCard(userGameStatus);
        
        } else if (optLevel.get().getLevelType() == LevelType.INFO) {
            // TODO IMPLEMENT IT!
        }
        
        // Set player to next level
        if (gameContinues) {
            gameContinues = gameCardService.moveUserToNextLevel(gameCardService.getUsernameFromSession(session), decisionAnswer.getLevel());
        }
        
        logger.info("Return after /make-decision call: " + gameContinues);
        return gameContinues;
    }
    
    @GetMapping("/last-level")
    @ResponseBody
    public int getLastLevel(HttpSession session, Model model) {
        return Level.getSIZE();
    }
    
    
    @GetMapping("/new-game")
    @ResponseBody
    public void startNewGame(HttpSession session, Model model) {
        logger.info("User called /new-game, will start new game in level 1.");
        
        gameCardService.startNewGame(gameCardService.getUsernameFromSession(session));
    }
    
    
    @GetMapping("/get-dialog")
    public String getDialogContent(HttpSession session, Model model) {
        
        // User picked new card- so update the level of the user
        UserGameStatus userGameStatus = gameCardService.getUserGameStatus(gameCardService.getUsernameFromSession(session));
        model.addAttribute("userGameStatus", userGameStatus);
        
        // check what kind of action will be next
        Optional<Level> optLevel = Level.findByLevel(userGameStatus.getLevel());
        if (optLevel.isPresent()) {
            LevelType nextLevelType = optLevel.get().getLevelType();
            switch (nextLevelType) {
                case INSURANCE:
                    userGameStatus = gameCardService.addInsuranceToUserGameStatus(userGameStatus, optLevel.get().getLevel());
                    model.addAttribute("userGameStatus", userGameStatus);
                    if (userGameStatus.getActualCardInsuranceName() == Insurance.LEGALPROTECTION_INSURANCE.getName()) {
                        return "decision/four_options_decision :: replace_fragment";
                    } else {
                        return "decision/triple_options_decision :: replace_fragment";
                    }
                case DECISION:
                    return "decision/two_options_decision :: replace_fragment";
                case INFO:
                    return "decision/simple_info :: replace_fragment";
                case ACTION:
                    
                    Optional<ActionCard> optNeutralActionCard = rollDicesForNeutralActionCard(userGameStatus);
                    if (optNeutralActionCard.isPresent()) {
                        ActionCard actionCard = optNeutralActionCard.get();
                        model.addAttribute("actionCard", actionCard);
                        return "decision/neutral_actioncard";
                    } else {
                        ActionCardText actionCardText = gameCardService.getNewRandomActionCard(userGameStatus);
                        logger.info("Will add new actionCardText to model: {}", actionCardText);
                        model.addAttribute("actionCardText", actionCardText);
                        return "decision/action_card :: replace_fragment";
                    }
                case GAMEOVER:
                    return "decision/game_over_dialog :: replace_fragment";
                default:
                    throw new AssertionError();
            }
        } else {
            logger.warn("Couldnt resolve the level: {}", userGameStatus.getLevel());
        }
        
        return null;
    }
    
    private Optional<ActionCard> rollDicesForNeutralActionCard(UserGameStatus userGameStatus) {
        Optional<ActionCard> returnValue = Optional.empty();
        
        Random rand = new Random();
        int randomNum = rand.nextInt(5) + 1;
        logger.info("Rolled dice for neutral actioncard: {}", randomNum);
        
        if (randomNum == 3) {
            List<ActionCard> allActionCards = actionCardRepository.findAll();
            List<ActionCard> alreadyDrawnActionCards = userGameStatus.getNeutralActionCards();
            
            List<ActionCard> newActionCards = allActionCards.stream().filter(ac -> !alreadyDrawnActionCards.contains(ac)).collect(Collectors.toList());
            
            if (newActionCards != null && !newActionCards.isEmpty()) {
                ActionCard nextCard = newActionCards.get(0);
                returnValue = Optional.of(nextCard);
                userGameStatus.addActionCard(nextCard);
                userGameStatusRepository.save(userGameStatus);
            } else {
                ActionCard nextCard = allActionCards.get(0);
                returnValue = Optional.of(nextCard);
                userGameStatus.setNeutralActionCards(new ArrayList<>());
                userGameStatus.addActionCard(nextCard);
                userGameStatusRepository.save(userGameStatus);
            }
        }
        
        return returnValue;
    }
    
    @GetMapping("/game-over")
    public String finishGame(HttpSession session, Model model) {

        String username = gameCardService.getUsernameFromSession(session);
        UserGameStatus userGameStatus = gameCardService.getUserGameStatus(username);
        if (!userGameStatus.isGameOver() && userGameStatus.getLevel() == Level.getSIZE()) {
            logger.info("Set user {} to game-over state right now.", username);
            gameCardService.setUserGameOver(username);
            return "redirect:/game-over";
        } else if (userGameStatus.isGameOver() && userGameStatus.getLevel() == Level.getSIZE()) {
            logger.info("User {} called /game-over endpoint again, but is still game-over. Just show game_over template.", username);
            return "redirect:/game-over";
        } else {
            logger.warn("User {} tried to call /game-over endpoint, but hasnt reached final level! Ignore and show game board.", username);
            return "redirect:/game";
        }
    }
    
}
