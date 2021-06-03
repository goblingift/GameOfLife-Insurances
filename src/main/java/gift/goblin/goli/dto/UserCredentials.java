/*
 * Copyright (C) 2020 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.goli.dto;


/**
 *
 * @author andre
 */
public class UserCredentials {
    
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "UserCredentials{" + "username=" + username + '}';
    }

}
