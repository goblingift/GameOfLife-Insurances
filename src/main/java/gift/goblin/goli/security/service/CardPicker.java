/*
 * Copyright (C) 2021 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.goli.security.service;

import gift.goblin.goli.database.model.UserGameStatus;
import gift.goblin.goli.database.repository.actioncards.CarInsuranceActionCardRepository;
import gift.goblin.goli.database.repository.actioncards.DisabilityInsuranceActionCardRepository;
import gift.goblin.goli.database.repository.actioncards.HouseholdInsuranceActionCardRepository;
import gift.goblin.goli.database.repository.actioncards.LiabilityInsuranceActionCardRepository;
import gift.goblin.goli.enumerations.Insurance;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This service contains logic to draw a new action-card for an user.
 *
 * @author andre
 */
@Service
public class CardPicker {

    @Autowired
    CarInsuranceActionCardRepository carInsuranceActionCardRepository;

    @Autowired
    LiabilityInsuranceActionCardRepository liabilityInsuranceActionCardRepository;

    @Autowired
    DisabilityInsuranceActionCardRepository disabilityInsuranceActionCardRepository;

    @Autowired
    HouseholdInsuranceActionCardRepository householdInsuranceActionCardRepository;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Will randomized pick a insurance-type for the next card.
     *
     * @param userGameStatus contains several information about game-progress of
     * user.
     * @return the type of action-card for the next-card which the user will
     * get.
     */
    public Insurance getNewRandomInsurance(UserGameStatus userGameStatus) {

        List<Insurance> possibleInsurances = Insurance.getValues().stream().filter(i -> i.getLevel() <= userGameStatus.getLevel())
                .collect(Collectors.toList());

        Random rand = new Random();
        Insurance randomElement = possibleInsurances.get(rand.nextInt(possibleInsurances.size()));

        logger.info("Randomized next Insurance-type: {}", randomElement);
        return randomElement;
    }

    /**
     * Picks up an action-card from a specific insurance, which wasn´t picked up
     * by the user before.
     *
     * @param userGameStatus
     * @param insurance
     * @return a new action-card id. (If user already picked all cards of that
     * insurance, take one card again)
     */
    public String getNewRandomActionCard(UserGameStatus userGameStatus, Insurance insurance) {

        logger.info("Draw new action-card of Insurance-type: {}", insurance.getName());

        switch (insurance) {
            case CAR_INSURANCE:
                return getNewRandomCardId(carInsuranceActionCardRepository.findAll().stream().map(i -> i.getId()).collect(Collectors.toList()), userGameStatus.getPickedActionCards());
            case DISABILITY_INSURANCE:
                return getNewRandomCardId(disabilityInsuranceActionCardRepository.findAll().stream().map(i -> i.getId()).collect(Collectors.toList()), userGameStatus.getPickedActionCards());
            case HOUSEHOLD_INSURANCE:
                return getNewRandomCardId(householdInsuranceActionCardRepository.findAll().stream().map(i -> i.getId()).collect(Collectors.toList()), userGameStatus.getPickedActionCards());
            case LIABILITY_INSURANCE:
                return getNewRandomCardId(liabilityInsuranceActionCardRepository.findAll().stream().map(i -> i.getId()).collect(Collectors.toList()), userGameStatus.getPickedActionCards());
        }
        
        logger.warn("Unknown insurance-type given: {}", insurance);
        return null;
    }

    /**
     * Will pick one random card of the given list of available cards. If user
     * already picked all cards, will return any random card again.
     *
     * @param possibleActionCardIds list with all available ids of the
     * actioncards.
     * @param alreadyPickedActionCardIds list with all picked actioncard-ids
     * which the user picked.
     * @return the id of the next action-card.
     */
    private String getNewRandomCardId(List<String> possibleActionCardIds, List<String> alreadyPickedActionCardIds) {

        Optional<String> optNewCard = possibleActionCardIds.stream().filter(a -> !alreadyPickedActionCardIds.contains(a)).findAny();
        if (optNewCard.isPresent()) {
            return optNewCard.get();
        } else {
            logger.info("User has already picked up all cards of that insurance-type, will return one random card again");
            Random rand = new Random();
            return possibleActionCardIds.get(rand.nextInt(possibleActionCardIds.size()));
        }
    }

}
