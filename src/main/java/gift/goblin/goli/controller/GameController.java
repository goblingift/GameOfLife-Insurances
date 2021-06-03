/*
 * Copyright (C) 2021 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.goli.controller;

import gift.goblin.goli.WebSecurityConfig;
import gift.goblin.goli.database.model.UserGameStatus;
import gift.goblin.goli.dto.DecisionAnswer;
import gift.goblin.goli.security.service.GameCardService;
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

        model.addAttribute("userGameStatus", getUserGameStatusFromSession(session));
        model.addAttribute("build_artifact", buildProperties.getArtifact());
        model.addAttribute("build_version", buildProperties.getVersion());
        return "game_board";
    }

    private UserGameStatus getUserGameStatusFromSession(HttpSession session) {
        return (UserGameStatus) session.getAttribute(WebSecurityConfig.SESSION_FIELD_USERGAMESTATUS);
    }

    private String getUsernameFromSession(HttpSession session) {
        return (String) session.getAttribute(WebSecurityConfig.SESSION_FIELD_USERNAME);
    }

    @PostMapping("/make-decision")
    public void submitDecision(DecisionAnswer decisionAnswer, HttpSession session) {

        logger.info("Called submitDecision with data: {}", decisionAnswer);
        gameCardService.handleUserDecision(getUsernameFromSession(session), decisionAnswer.getLevel(), decisionAnswer.getAnswer());
    }

}
