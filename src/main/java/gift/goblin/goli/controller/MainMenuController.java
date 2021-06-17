/*
 * Copyright (C) 2020 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.goli.controller;

import gift.goblin.goli.WebSecurityConfig;
import gift.goblin.goli.dto.GameStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 *
 * @author andre
 */
@Controller
public class MainMenuController {

    @Autowired
    private BuildProperties buildProperties;

    @Autowired
    private GameStatus gameStatus;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @GetMapping(value = {"/home", "/"})
    public String renderMainMenu(Model model, Authentication authentication) {

        logger.info("User opened main-menu. Game status is: {}", gameStatus.isGameEnabled());

        if (isUserAdmin(authentication)) {
            logger.info("User has admin-role, display more options for em.");
            model.addAttribute("isAdmin", true);
        }

        model.addAttribute("createCards", gameStatus.isCreateCards());
        model.addAttribute("gameStarted", gameStatus.isGameEnabled());
        model.addAttribute("build_artifact", buildProperties.getArtifact());
        model.addAttribute("build_version", buildProperties.getVersion());
        return "main_menu";
    }

    private boolean isUserAdmin(Authentication authentication) {
        if (authentication != null && authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equalsIgnoreCase(WebSecurityConfig.ROLE_ADMIN))) {
            return true;
        } else {
            return false;
        }
    }

    @GetMapping(value = "/start-card-creation")
    public String startCardCreation(Model model, Authentication authentication) {

        if (isUserAdmin(authentication)) {
            logger.info("Start card creation now.");
            gameStatus.setCreateCards(true);
        } else {
            logger.warn("User is no admin- wont start card-creation now.");
        }

        return "redirect:/home";
    }

    @GetMapping(value = "/stop-card-creation")
    public String stopCardCreation(Model model, Authentication authentication) {

        if (isUserAdmin(authentication)) {
            logger.info("Stop card creation now.");
            gameStatus.setCreateCards(false);
        } else {
            logger.warn("User is no admin- wont stop card-creation now.");
        }

        return "redirect:/home";
    }

    @GetMapping(value = "/start-game")
    public String startGame(Model model, Authentication authentication) {

        if (isUserAdmin(authentication)) {
            logger.info("Game starts now!");
            gameStatus.setGameEnabled(true);
        } else {
            logger.warn("User is no admin- wont start game now.");
        }

        return "redirect:/home";
    }

    @GetMapping(value = "/stop-game")
    public String stopGame(Model model, Authentication authentication) {

        if (isUserAdmin(authentication)) {
            logger.info("Game stops now!");
            gameStatus.setGameEnabled(false);
        } else {
            logger.warn("User is no admin- wont stop game now.");
        }

        return "redirect:/home";
    }

}
