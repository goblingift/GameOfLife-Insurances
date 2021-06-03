/*
 * Copyright (C) 2021 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.goli.security.service;

import gift.goblin.goli.database.model.ContractedInsurance;
import gift.goblin.goli.database.model.User;
import gift.goblin.goli.database.model.UserGameStatus;
import gift.goblin.goli.database.repository.UserGameStatusRepository;
import gift.goblin.goli.enumerations.Insurance;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Contains logic to handle processes related to the game cards (decisions and
 * action-cards).
 *
 * @author andre
 */
@Service
public class GameCardService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserGameStatusRepository userGameStatusRepository;
    
    @Autowired
    private CustomUserDetailsService userService;

    /**
     * Handles the logic if an user answered a decision- or action-card.
     *
     * @param username username of the user.
     * @param level the current level of the user.
     * @param answer the selected answer of the user.
     * @return true if successful, or false if otherwise.
     */
    public boolean handleUserDecision(String username, int level, int answer) {

        UserGameStatus userGameStatus = userGameStatusRepository.findByUsername(username);
        User user = userService.findUserByFullname(username);
        
        if (user == null) {
            logger.warn("No user found by username: {}", username);
            return false;
        }
        
        if (userGameStatus.getLevel() != level) {
            logger.warn("User {} tried to submit decision for another level ({}) than current level ({})", username, level, userGameStatus.getLevel());
            return false;
        }

        Optional<Insurance> optInsurance = Insurance.findByLevel(level);
        if (optInsurance.isPresent()) {

            Insurance insurance = optInsurance.get();
            double yearlyCost;
            switch (answer) {
                case 1:
                    yearlyCost = insurance.getPriceYearly1();
                    break;
                case 2:
                    yearlyCost = insurance.getPriceYearly2();
                    break;
                case 3:
                    yearlyCost = insurance.getPriceYearly3();
                    break;    
                default:
                    yearlyCost = 0.0;
            }
            
            ContractedInsurance contractedInsurance = new ContractedInsurance(UUID.randomUUID().toString(), user, insurance.getId(), yearlyCost, answer);
            userGameStatus.addContractedInsurance(contractedInsurance);
            userGameStatusRepository.save(userGameStatus);
            
            return true;
        } else {
            logger.warn("No Insurance found for the given level ({})!", level);
            return false;
        }
        
    }

}
