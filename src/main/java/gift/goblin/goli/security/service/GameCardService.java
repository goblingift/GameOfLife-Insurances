/*
 * Copyright (C) 2021 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.goli.security.service;

import gift.goblin.goli.database.model.ContractedInsurance;
import gift.goblin.goli.database.model.User;
import gift.goblin.goli.database.model.UserGameStatus;
import gift.goblin.goli.database.model.actioncards.CarInsuranceActionCard;
import gift.goblin.goli.database.model.actioncards.DisabilityInsuranceActionCard;
import gift.goblin.goli.database.repository.ContractedInsuranceRepository;
import gift.goblin.goli.database.repository.UserGameStatusRepository;
import gift.goblin.goli.database.repository.actioncards.CarInsuranceActionCardRepository;
import gift.goblin.goli.database.repository.actioncards.DisabilityInsuranceActionCardRepository;
import gift.goblin.goli.database.repository.actioncards.HouseholdInsuranceActionCardRepository;
import gift.goblin.goli.database.repository.actioncards.LiabilityInsuranceActionCardRepository;
import gift.goblin.goli.dto.ActionCardText;
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
    
    @Autowired
    private ContractedInsuranceRepository contractedInsuranceRepository;

    @Autowired
    CarInsuranceActionCardRepository carInsuranceActionCardRepository;

    @Autowired
    LiabilityInsuranceActionCardRepository liabilityInsuranceActionCardRepository;

    @Autowired
    DisabilityInsuranceActionCardRepository disabilityInsuranceActionCardRepository;

    @Autowired
    HouseholdInsuranceActionCardRepository householdInsuranceActionCardRepository;
    
    @Autowired
    ActionCardTextConverter actionCardTextConverter;
    
    @Autowired
    private CardPicker cardPicker;
    
    
    public UserGameStatus getUserGameStatus(String username) {
        UserGameStatus userGameStatus = userGameStatusRepository.findByUsername(username);
        return userGameStatus;
    }
    
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
            
            // check if user already approved this insurance
            Optional<ContractedInsurance> optContractedInsurance = contractedInsuranceRepository.findByUserAndInsuranceId(user, insurance.getId());
            if (optContractedInsurance.isPresent()) {
                ContractedInsurance contractedInsurance = optContractedInsurance.get();
                contractedInsurance.setSelectedChoice(answer);
                contractedInsurance.setYearlyCost(yearlyCost);
                contractedInsuranceRepository.save(contractedInsurance);
            } else {
                ContractedInsurance contractedInsurance = new ContractedInsurance(UUID.randomUUID().toString(), user, insurance.getId(), yearlyCost, answer);
                contractedInsuranceRepository.save(contractedInsurance);
                userGameStatus.addContractedInsurance(contractedInsurance);
                userGameStatusRepository.save(userGameStatus);
            }
            
            return true;
        } else {
            logger.warn("No Insurance found for the given level ({})!", level);
            return false;
        }
        
    }
    
    /**
     * Moves the user to next level.
     * @param username the username.
     * @param level the current level of the user.
     * @return true if successful, false if e.g. the given level doesnt equal the current level of users gamestatus in DB.
     */
    public boolean moveUserToNextLevel(String username, int level) {
        
        UserGameStatus userGameStatus = userGameStatusRepository.findByUsername(username);
        
        if (userGameStatus.getLevel() != level) {
            logger.warn("Couldnt move user to next level, because current game level ({}) isnt equal like the saved level in DB: {}",
                    level, userGameStatus.getLevel());
            return false;
        }
        
        int nextLevel = level + 1;
        userGameStatus.setLevel(nextLevel);
        userGameStatusRepository.save(userGameStatus);
        logger.info("Successful set user {} to next level in DB: {}", username, nextLevel);
        
        return true;
    }
    
    
    public ActionCardText getNewRandomActionCard(UserGameStatus userGameStatus) {
        
        Insurance nextInsuranceType = cardPicker.getNewRandomInsurance(userGameStatus);
        String newRandomActionCardId = cardPicker.getNewRandomActionCard(userGameStatus, nextInsuranceType);
        
        
        switch (nextInsuranceType) {
            case CAR_INSURANCE:
                Optional<CarInsuranceActionCard> optCarInsuranceActionCard = carInsuranceActionCardRepository.findById(newRandomActionCardId);
                return actionCardTextConverter.convertToActionCardText(optCarInsuranceActionCard.get(), userGameStatus);
            case DISABILITY_INSURANCE:
            Optional<DisabilityInsuranceActionCard> optDisabilityInsuranceActionCard = disabilityInsuranceActionCardRepository.findById(newRandomActionCardId);
                return actionCardTextConverter.convertToActionCardText(optDisabilityInsuranceActionCard.get(), userGameStatus);
            case HOUSEHOLD_INSURANCE:
            case LIABILITY_INSURANCE:
        }
        
        
        return null;
    }

}
