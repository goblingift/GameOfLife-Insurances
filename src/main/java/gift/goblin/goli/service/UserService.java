/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gift.goblin.goli.service;

import gift.goblin.goli.WebSecurityConfig;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

/**
 *
 * @author andre
 */
@Service
public class UserService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    
    public boolean isUserAdmin(Authentication authentication) {
        if (authentication != null && containsAdminRole(authentication.getAuthorities())) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isUserAdmin(UserDetails userDetails) {
        if (userDetails != null && containsAdminRole(userDetails.getAuthorities())) {
            return true;
        } else {
            return false;
        }
    }

    private boolean containsAdminRole(Collection<? extends GrantedAuthority> authorities) {
        if (authorities.stream().anyMatch(a -> a.getAuthority().equalsIgnoreCase(WebSecurityConfig.ROLE_ADMIN))) {
            return true;
        } else {
            return false;
        }
    }

}
