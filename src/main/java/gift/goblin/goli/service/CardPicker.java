/*
 * Copyright (C) 2021 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.goli.service;

import gift.goblin.goli.database.model.UserGameStatus;
import gift.goblin.goli.database.repository.actioncards.CarInsuranceActionCardRepository;
import gift.goblin.goli.database.repository.actioncards.DisabilityInsuranceActionCardRepository;
import gift.goblin.goli.database.repository.actioncards.HomeInsuranceActionCardRepository;
import gift.goblin.goli.database.repository.actioncards.HouseholdInsuranceActionCardRepository;
import gift.goblin.goli.database.repository.actioncards.LegalProtectionInsuranceActionCardRepository;
import gift.goblin.goli.database.repository.actioncards.LiabilityInsuranceActionCardRepository;
import gift.goblin.goli.database.repository.actioncards.SeniorAccidentInsuranceActionCardRepository;
import gift.goblin.goli.database.repository.actioncards.SmartphoneInsuranceActionCardRepository;
import gift.goblin.goli.database.repository.actioncards.TermLifeInsuranceActionCardRepository;
import gift.goblin.goli.dto.InsuranceWithType;
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

    @Autowired
    SmartphoneInsuranceActionCardRepository smartphoneInsuranceActionCardRepository;

    @Autowired
    HomeInsuranceActionCardRepository homeInsuranceActionCardRepository;

    @Autowired
    TermLifeInsuranceActionCardRepository termLifeInsuranceActionCardRepository;

    @Autowired
    SeniorAccidentInsuranceActionCardRepository seniorAccidentInsuranceActionCardRepository;

    @Autowired
    LegalProtectionInsuranceActionCardRepository legalProtectionInsuranceActionCardRepository;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Will randomized pick a insurance-type for the next card.
     *
     * @param userGameStatus contains several information about game-progress of
     * user.
     * @return the type of action-card for the next-card which the user will
     * get.
     */
    @Deprecated
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
    @Deprecated
    public String getNewRandomActionCard(UserGameStatus userGameStatus, Insurance insurance) {

        logger.info("Draw new action-card of Insurance-type: {}", insurance.getName());

        switch (insurance) {
            case CAR_INSURANCE:
                return getNewRandomCardId(carInsuranceActionCardRepository.findAll().stream().map(i -> i.getId()).collect(Collectors.toList()), 
                        userGameStatus.getDamageCases().stream().map(dc -> dc.getActionCardId()).collect(Collectors.toList()));
            case DISABILITY_INSURANCE:
                return getNewRandomCardId(disabilityInsuranceActionCardRepository.findAll().stream().map(i -> i.getId()).collect(Collectors.toList()),
                        userGameStatus.getDamageCases().stream().map(dc -> dc.getActionCardId()).collect(Collectors.toList()));
            case HOUSEHOLD_INSURANCE:
                return getNewRandomCardId(householdInsuranceActionCardRepository.findAll().stream().map(i -> i.getId()).collect(Collectors.toList()), 
                        userGameStatus.getDamageCases().stream().map(dc -> dc.getActionCardId()).collect(Collectors.toList()));
            case LIABILITY_INSURANCE:
                return getNewRandomCardId(liabilityInsuranceActionCardRepository.findAll().stream().map(i -> i.getId()).collect(Collectors.toList()),
                        userGameStatus.getDamageCases().stream().map(dc -> dc.getActionCardId()).collect(Collectors.toList()));
            case SMARTPHONE_INSURANCE:
                return getNewRandomCardId(smartphoneInsuranceActionCardRepository.findAll().stream().map(i -> i.getId()).collect(Collectors.toList()),
                        userGameStatus.getDamageCases().stream().map(dc -> dc.getActionCardId()).collect(Collectors.toList()));
            case HOME_INSURANCE:
                return getNewRandomCardId(homeInsuranceActionCardRepository.findAll().stream().map(i -> i.getId()).collect(Collectors.toList()), 
                        userGameStatus.getDamageCases().stream().map(dc -> dc.getActionCardId()).collect(Collectors.toList()));
            case TERMLIFE_INSURANCE:
                return getNewRandomCardId(termLifeInsuranceActionCardRepository.findAll().stream().map(i -> i.getId()).collect(Collectors.toList()),
                        userGameStatus.getDamageCases().stream().map(dc -> dc.getActionCardId()).collect(Collectors.toList()));
            case LEGALPROTECTION_INSURANCE:
                return getNewRandomCardId(legalProtectionInsuranceActionCardRepository.findAll().stream().map(i -> i.getId()).collect(Collectors.toList()),
                        userGameStatus.getDamageCases().stream().map(dc -> dc.getActionCardId()).collect(Collectors.toList()));
            case SENIORACCIDENT_INSURANCE:
                return getNewRandomCardId(seniorAccidentInsuranceActionCardRepository.findAll().stream().map(i -> i.getId()).collect(Collectors.toList()), 
                        userGameStatus.getDamageCases().stream().map(dc -> dc.getActionCardId()).collect(Collectors.toList()));

        }

        logger.error("Unknown insurance-type given: {}", insurance);
        return null;
    }

    /**
     * Picks a new random actioncard, which werent already drawn by the player.
     * @param userGameStatus required to get list of already played actioncards.
     * @return the id and the type of the drawn actioncard.
     */
    public InsuranceWithType getNewRandomActionCard(UserGameStatus userGameStatus) {

        List<InsuranceWithType> allActionCards = new ArrayList<>();
        
        if (userGameStatus.getLevel() > Insurance.CAR_INSURANCE.getLevel()) {
            List<InsuranceWithType> carInsuranceActionCards = carInsuranceActionCardRepository.findAll().stream().map(ac -> new InsuranceWithType(ac.getId(), Insurance.CAR_INSURANCE)).collect(Collectors.toList());
            allActionCards.addAll(carInsuranceActionCards);
        }
        
        if (userGameStatus.getLevel() > Insurance.DISABILITY_INSURANCE.getLevel()) {
            List<InsuranceWithType> disabilityInsuranceActionCards = disabilityInsuranceActionCardRepository.findAll().stream().map(ac -> new InsuranceWithType(ac.getId(), Insurance.DISABILITY_INSURANCE)).collect(Collectors.toList());
            allActionCards.addAll(disabilityInsuranceActionCards);
        }

        if (userGameStatus.getLevel() > Insurance.HOUSEHOLD_INSURANCE.getLevel()) {
            List<InsuranceWithType> householdInsuranceActionCards = householdInsuranceActionCardRepository.findAll().stream().map(ac -> new InsuranceWithType(ac.getId(), Insurance.HOUSEHOLD_INSURANCE)).collect(Collectors.toList());
            allActionCards.addAll(householdInsuranceActionCards);
        }

        if (userGameStatus.getLevel() > Insurance.LIABILITY_INSURANCE.getLevel()) {
            List<InsuranceWithType> liabilityInsuranceActionCards = liabilityInsuranceActionCardRepository.findAll().stream().map(ac -> new InsuranceWithType(ac.getId(), Insurance.LIABILITY_INSURANCE)).collect(Collectors.toList());
            allActionCards.addAll(liabilityInsuranceActionCards);
        }

        if (userGameStatus.getLevel() > Insurance.SMARTPHONE_INSURANCE.getLevel()) {
            List<InsuranceWithType> smartphoneInsuranceActionCards = smartphoneInsuranceActionCardRepository.findAll().stream().map(ac -> new InsuranceWithType(ac.getId(), Insurance.SMARTPHONE_INSURANCE)).collect(Collectors.toList());
            allActionCards.addAll(smartphoneInsuranceActionCards);
        }

        if (userGameStatus.getLevel() > Insurance.HOME_INSURANCE.getLevel()) {
            List<InsuranceWithType> homeInsuranceActionCards = homeInsuranceActionCardRepository.findAll().stream().map(ac -> new InsuranceWithType(ac.getId(), Insurance.HOME_INSURANCE)).collect(Collectors.toList());
            allActionCards.addAll(homeInsuranceActionCards);
        }
        
        if (userGameStatus.getLevel() > Insurance.TERMLIFE_INSURANCE.getLevel()) {
            List<InsuranceWithType> termLifeInsuranceActionCards = termLifeInsuranceActionCardRepository.findAll().stream().map(ac -> new InsuranceWithType(ac.getId(), Insurance.TERMLIFE_INSURANCE)).collect(Collectors.toList());
            allActionCards.addAll(termLifeInsuranceActionCards);
        }

        if (userGameStatus.getLevel() > Insurance.LEGALPROTECTION_INSURANCE.getLevel()) {
            List<InsuranceWithType> legalProtectionInsuranceActionCards = legalProtectionInsuranceActionCardRepository.findAll().stream().map(ac -> new InsuranceWithType(ac.getId(), Insurance.LEGALPROTECTION_INSURANCE)).collect(Collectors.toList());
            allActionCards.addAll(legalProtectionInsuranceActionCards);
        }

        if (userGameStatus.getLevel() > Insurance.SENIORACCIDENT_INSURANCE.getLevel()) {
            List<InsuranceWithType> seniorAccidentInsuranceActionCards = seniorAccidentInsuranceActionCardRepository.findAll().stream().map(ac -> new InsuranceWithType(ac.getId(), Insurance.SENIORACCIDENT_INSURANCE)).collect(Collectors.toList());
            allActionCards.addAll(seniorAccidentInsuranceActionCards);
        }
        
        // get all actioncards, which were already drawn by the user
        List<String> playedActionCardIds = userGameStatus.getDamageCases().stream().map(dc -> dc.getActionCardId()).collect(Collectors.toList());

        List<InsuranceWithType> newActionCardIds = allActionCards.stream().filter(ac -> !playedActionCardIds.contains(ac.getId())).collect(Collectors.toList());
        Random rand = new Random();
        return newActionCardIds.get(rand.nextInt(newActionCardIds.size()));
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
