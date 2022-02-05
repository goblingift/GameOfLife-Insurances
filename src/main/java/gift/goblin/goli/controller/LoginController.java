/*
 * Copyright (C) 2021 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.goli.controller;

import gift.goblin.goli.WebSecurityConfig;
import gift.goblin.goli.database.model.UserGameStatus;
import gift.goblin.goli.database.repository.UserGameStatusRepository;
import gift.goblin.goli.dto.UserCredentials;
import gift.goblin.goli.service.CustomUserDetailsService;
import gift.goblin.goli.service.UserService;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.thymeleaf.util.StringUtils;

/**
 * Handles the login of users.
 *
 * @author andre
 */
@Controller
@RequestMapping("/login")
public class LoginController {
    
    private static final String ATTRIBUTE_ISADMIN = "isAdmin";
    private static final String ATTRIBUTE_USERNAME = "username";
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    private BuildProperties buildProperties;
    
    @Autowired
    private CustomUserDetailsService customUserService;
    
    @Autowired
    private UserGameStatusRepository userGameStatusRepository;
    
    @Autowired
    private UserService userService;

    @GetMapping
    public String renderLoginForm(Model model) {
        logger.info("User called loginpage.");
        
        if (model.containsAttribute(ATTRIBUTE_ISADMIN)) {
            logger.info("isAdmin = true");
            model.addAttribute(ATTRIBUTE_ISADMIN, true);
            model.addAttribute("userForm", new UserCredentials(model.getAttribute(ATTRIBUTE_USERNAME).toString()));
        } else {
            model.addAttribute(ATTRIBUTE_USERNAME, "");
            model.addAttribute("userForm", new UserCredentials());
        }
        
        model.addAttribute("build_artifact", buildProperties.getArtifact());
        model.addAttribute("build_version", buildProperties.getVersion());
        return "login";
    }

    @PostMapping(path = "/submit")
    public String loginUser(HttpSession session, @ModelAttribute("userForm") UserCredentials userForm, BindingResult bindingResult, Model model) {
        logger.info("User submitted login-form: {}", userForm);
        
        UserDetails userDetails = null;
        try {
            userDetails = customUserService.loadUserByUsername(userForm.getUsername());
        } catch (UsernameNotFoundException e) {
            logger.warn("Couldnt find user by username: {}", userForm.getUsername());
        }
        
        if (userDetails != null) {
            
            // check if user is admin- then he needs to enter password to login
            if (userService.isUserAdmin(userDetails)) {
                if (StringUtils.isEmptyOrWhitespace(userDetails.getPassword())) {
                    return displayPasswordInputAdminUser(model, userDetails.getUsername());
                } else {
                    // todo LOGIN ADMIN
                    return null;
                }
            } else {
                return loginCommonUser(userDetails, session);
            }
        } else {
            return renderLoginForm(model);
        }
    }

    private String displayPasswordInputAdminUser(Model model, String username) {
        logger.info("Admin user {} tries to login- reload page with password input.");
        model.addAttribute(ATTRIBUTE_ISADMIN, "true");
        model.addAttribute(ATTRIBUTE_USERNAME, username);
        return renderLoginForm(model);
    }

    private String loginCommonUser(UserDetails userDetails, HttpSession session) {
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
        
        UserGameStatus userGameStatus = userGameStatusRepository.findByUsername(userDetails.getUsername());
        if (userGameStatus == null) {
            userGameStatus = createNewUserGameStatus(userDetails.getUsername());
        }
        
        session.setAttribute(WebSecurityConfig.SESSION_FIELD_USERNAME, userDetails.getUsername());
        logger.info("Successful set username to session: {}", userDetails.getUsername());
        
        return "redirect:/home";
    }
    
    private String loginAdminUser(UserDetails userDetails, HttpSession session) {
        
        
        
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
