/*
 * Copyright (C) 2021 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.goli.controller;

import gift.goblin.goli.WebSecurityConfig;
import gift.goblin.goli.database.model.UserGameStatus;
import gift.goblin.goli.database.repository.UserGameStatusRepository;
import gift.goblin.goli.dto.UserCredentials;
import gift.goblin.goli.security.service.CustomUserDetailsService;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Handles the login of users.
 *
 * @author andre
 */
@Controller
@RequestMapping("/login")
public class LoginController {
    
    @Autowired
    private BuildProperties buildProperties;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CustomUserDetailsService userService;
    
    @Autowired
    private UserGameStatusRepository userGameStatusRepository;

    @GetMapping
    public String renderRegistrationForm(Model model) {
        logger.info("User called loginpage.");
        
        model.addAttribute("userForm", new UserCredentials());
        
        model.addAttribute("build_artifact", buildProperties.getArtifact());
        model.addAttribute("build_version", buildProperties.getVersion());
        return "login";
    }

    @PostMapping(path = "/submit")
    public String registration(HttpSession session, @ModelAttribute("userForm") UserCredentials userForm, BindingResult bindingResult, Model model) {
        logger.info("User submitted login-form: {}", userForm);
        

        UserDetails userDetails = userService.loadUserByUsername(userForm.getUsername());
        if (userDetails != null) {
            Authentication auth = new UsernamePasswordAuthenticationToken(userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);
            
            UserGameStatus userGameStatus = userGameStatusRepository.findByUsername(userDetails.getUsername());
            if (userGameStatus == null) {
                userGameStatus = createNewUserGameStatus(userDetails.getUsername());
            } else {
                logger.info("Successful found existing UserGameStatus object in database- use them for this game: {}", userGameStatus);
            }
            session.setAttribute(WebSecurityConfig.SESSION_FIELD_USERGAMESTATUS, userGameStatus);

            session.setAttribute(WebSecurityConfig.SESSION_FIELD_USERNAME, userForm.getUsername());
            logger.info("Successful set username to session: {}", userForm.getUsername());
            
            return "redirect:/home";
        } else {
            return "redirect:/login";
        }
        
    }
    
    /**
     * Creates new UserGameStatus and saves into database.
     * @param username
     * @return the created entity.
     */
    private UserGameStatus createNewUserGameStatus(String username) {
        UserGameStatus newEntity = new UserGameStatus(username, 1);
        userGameStatusRepository.save(newEntity);
        
        logger.info("Successful created new UserGameStatus in database for user: {}", username);
        return newEntity;
    }

}
