/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gift.goblin.goli.service;

import gift.goblin.goli.database.model.Role;
import gift.goblin.goli.database.model.User;
import gift.goblin.goli.database.repository.RoleRepository;
import gift.goblin.goli.database.repository.UserRepository;
import java.util.Optional;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 *
 * @author andre
 */
@Service
public class DatabaseInitializer {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Value("${user.admin.defaultpw}")
    private String adminDefaultPw;

    @Value("${user.admin.username}")
    private String adminUsername;

    /**
     * Checks if the default roles are available in database. Create them if not
     * found.
     */
    public void initializeRoles() {
        logger.info("Oh look:" + adminDefaultPw);
        createRoleIfNotFound(Role.ROLE_ID_ADMIN, Role.ROLE_ADMIN);
        createRoleIfNotFound(Role.ROLE_ID_USER, Role.ROLE_USER);
    }

    /**
     * Checks if the default admin user is available in database. Create em if
     * not found. Username can be defined in application.properties file:
     * 'user.admin.username'. Default password will be used from
     * application.properties file: 'user.admin.defaultpw'.
     */
    public void initializeAdminUser() {
        User findByFullname = userRepository.findByFullname(adminUsername);
        if (findByFullname == null) {
            logger.info("Couldnt find admin-user by its username: {} - will create em now in database.", adminUsername);
            Optional<Role> adminRole = roleRepository.findById(Role.ROLE_ID_ADMIN);
            if (adminRole.isPresent()) {
                User newUser = new User(User.ID_ADMIN, adminDefaultPw, adminUsername, Set.of(adminRole.get()));
                logger.info("Will create new admin user right now: {}", newUser);
                userRepository.save(newUser);
            } else {
                logger.warn("No role ADMIN found- cant create admin-user right now!");
            }
        }
        
    }

    private void createRoleIfNotFound(String roleId, String roleName) {
        Optional<Role> role = roleRepository.findByRole(roleName);
        if (role.isEmpty()) {
            logger.info("Couldnt find role {} - will create new one in database now.", roleName);
            Role newRole = new Role();
            newRole.setId(roleId);
            newRole.setRole(roleName);
            logger.info("Will create new role in DB: {}", newRole);
            roleRepository.save(newRole);
        }
    }

}
