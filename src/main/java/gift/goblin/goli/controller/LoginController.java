/*
 * Copyright (C) 2021 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.goli.controller;

import gift.goblin.goli.WebSecurityConfig;
import gift.goblin.goli.dto.UserGameStatus;
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
        
        session.setAttribute(WebSecurityConfig.SESSION_FIELD_GAMESTATUS, new UserGameStatus(userForm.getUsername()));
        logger.info("Successful set username to session: {}", userForm.getUsername());

        UserDetails userDetails = userService.loadUserByUsername(userForm.getUsername());

        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        return "redirect:/home";
    }

}
