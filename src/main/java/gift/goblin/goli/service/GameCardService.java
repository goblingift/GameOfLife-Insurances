/*
 * Copyright (C) 2021 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.goli.service;

import gift.goblin.goli.WebSecurityConfig;
import gift.goblin.goli.database.model.ContractedInsurance;
import gift.goblin.goli.database.model.DamageCase;
import gift.goblin.goli.database.model.User;
import gift.goblin.goli.database.model.UserGameStatus;
import gift.goblin.goli.database.model.actioncards.CarInsuranceActionCard;
import gift.goblin.goli.database.model.actioncards.DisabilityInsuranceActionCard;
import gift.goblin.goli.database.model.actioncards.HomeInsuranceActionCard;
import gift.goblin.goli.database.model.actioncards.HouseholdInsuranceActionCard;
import gift.goblin.goli.database.model.actioncards.LegalProtectionInsuranceActionCard;
import gift.goblin.goli.database.model.actioncards.LiabilityInsuranceActionCard;
import gift.goblin.goli.database.model.actioncards.SeniorAccidentInsuranceActionCard;
import gift.goblin.goli.database.model.actioncards.SmartphoneInsuranceActionCard;
import gift.goblin.goli.database.model.actioncards.TermLifeInsuranceActionCard;
import gift.goblin.goli.database.repository.ContractedInsuranceRepository;
import gift.goblin.goli.database.repository.DamageCaseRepository;
import gift.goblin.goli.database.repository.UserGameStatusRepository;
import gift.goblin.goli.database.repository.UserRepository;
import gift.goblin.goli.database.repository.actioncards.CarInsuranceActionCardRepository;
import gift.goblin.goli.database.repository.actioncards.DisabilityInsuranceActionCardRepository;
import gift.goblin.goli.database.repository.actioncards.HomeInsuranceActionCardRepository;
import gift.goblin.goli.database.repository.actioncards.HouseholdInsuranceActionCardRepository;
import gift.goblin.goli.database.repository.actioncards.LegalProtectionInsuranceActionCardRepository;
import gift.goblin.goli.database.repository.actioncards.LiabilityInsuranceActionCardRepository;
import gift.goblin.goli.database.repository.actioncards.SeniorAccidentInsuranceActionCardRepository;
import gift.goblin.goli.database.repository.actioncards.SmartphoneInsuranceActionCardRepository;
import gift.goblin.goli.database.repository.actioncards.TermLifeInsuranceActionCardRepository;
import gift.goblin.goli.dto.ActionCardText;
import gift.goblin.goli.dto.GameOverSummary;
import gift.goblin.goli.dto.InsuranceWithType;
import gift.goblin.goli.enumerations.Insurance;
import gift.goblin.goli.enumerations.Level;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
    SmartphoneInsuranceActionCardRepository smartphoneInsuranceActionCardRepository;
    
    @Autowired
    HomeInsuranceActionCardRepository homeInsuranceActionCardRepository;
    
    @Autowired
    CarInsuranceActionCardRepository carInsuranceActionCardRepository;

    @Autowired
    TermLifeInsuranceActionCardRepository termLifeInsuranceActionCardRepository;
    
    @Autowired
    LiabilityInsuranceActionCardRepository liabilityInsuranceActionCardRepository;

    @Autowired
    DisabilityInsuranceActionCardRepository disabilityInsuranceActionCardRepository;

    @Autowired
    HouseholdInsuranceActionCardRepository householdInsuranceActionCardRepository;
    
    @Autowired
    SeniorAccidentInsuranceActionCardRepository seniorAccidentInsuranceActionCardRepository;

    @Autowired
    LegalProtectionInsuranceActionCardRepository legalProtectionInsuranceActionCardRepository;
    
    @Autowired
    ActionCardTextConverter actionCardTextConverter;
    
    @Autowired
    DamageCaseRepository damageCaseRepository;
    
    @Autowired
    UserRepository userRepository;
    
    @Autowired
    private CardPicker cardPicker;
    
    
    public UserGameStatus getUserGameStatus(String username) {
        UserGameStatus userGameStatus = userGameStatusRepository.findByUsername(username);
        return userGameStatus;
    }
    
    public void setUserGameOver(String username) {
        logger.info("Will set user {} to game-over.", username);
        
        UserGameStatus userGameStatus = getUserGameStatus(username);
        userGameStatus.setGameOver(true);
        userGameStatusRepository.save(userGameStatus);
    }
    
    public void startNewGame(String username) {
        logger.info("Starting new game for user: {}", username);
        UserGameStatus userGameStatus = userGameStatusRepository.findByUsername(username);
        userGameStatus.setLevel(1);
        userGameStatus.setGameOver(false);
        userGameStatus.setActualCardId(null);
        userGameStatus.setActualCardInsuranceName(null);
        userGameStatus.setContractedInsurances(new ArrayList<>());
        userGameStatus.setPaidForClaims(0);
        userGameStatus.setPaidForInsurances(0);
        userGameStatus.setDamageCases(new ArrayList<>());
        userGameStatusRepository.save(userGameStatus);
        
        // also delete all insurances of that user
        User user = userRepository.findByFullname(username);
        List<ContractedInsurance> contracts = contractedInsuranceRepository.findByUser(user);
        contracts.forEach(c -> contractedInsuranceRepository.delete(c));
        
        // also delete all damage cases of that user
        List<DamageCase> damageCases = damageCaseRepository.findByUser(user);
        damageCases.forEach(c -> damageCaseRepository.delete(c));
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
            
            
            if (insurance == Insurance.LEGALPROTECTION_INSURANCE && answer == 1) {
                logger.info("User choosed legalprotection-insurance with answer 1- set yearlyCosts to 0.00");
                yearlyCost = 0.00;
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
     * Will handle the currently active action-card for the user.
     * @param userGameStatus will be used to get the current action-card of user.
     * @return true if everything is fine and game continues. False if game ends (Player died e.g.).
     */
    public boolean handleActionCard(UserGameStatus userGameStatus) {
        
        String actualCardId = userGameStatus.getActualCardId();
        String actualCardInsuranceId = userGameStatus.getActualCardInsuranceName();
        
        if (StringUtils.hasText(actualCardId) && StringUtils.hasText(actualCardInsuranceId)) {
            Optional<Insurance> optInsurance = Insurance.getValues().stream().filter(i -> i.getName().equalsIgnoreCase(actualCardInsuranceId))
                    .findFirst();
            switch(optInsurance.get()) {
                case CAR_INSURANCE:
                    handleCarInsuranceActionCard(actualCardId, userGameStatus);
                    return true;
                case LIABILITY_INSURANCE:
                    handleLiabilityInsuranceActionCard(actualCardId, userGameStatus);
                    return true;
                case DISABILITY_INSURANCE:
                    handleDisabilityInsuranceActionCard(actualCardId, userGameStatus);
                    return true;
                case HOUSEHOLD_INSURANCE:
                    handleHouseholdInsuranceActionCard(actualCardId, userGameStatus);
                    return true;
                case SMARTPHONE_INSURANCE:
                    handleSmartphoneInsuranceActionCard(actualCardId, userGameStatus);
                    return true;
                case HOME_INSURANCE:
                    handleHomeInsuranceActionCard(actualCardId, userGameStatus);
                    return true;
                case TERMLIFE_INSURANCE:
                    handleTermLifeInsuranceActionCard(actualCardId, userGameStatus);
                    return false;
                case LEGALPROTECTION_INSURANCE:
                    handleLegalProtectionInsuranceActionCard(actualCardId, userGameStatus);
                    return true;
                case SENIORACCIDENT_INSURANCE:
                    handleSeniorAccidentInsuranceActionCard(actualCardId, userGameStatus);
                    return true;
            }
            
        } else {
            logger.warn("No action-card is active, something went wrong! Skip handling action-card for user: {}", userGameStatus);
            return true;
        }
        
        return true;
    }
    
    /**
     * Handles the creation of actioncard for car-insurance.
     * @param actionCardId
     * @param userGameStatus 
     */
    private void handleCarInsuranceActionCard(String actionCardId, UserGameStatus userGameStatus) {
        
        Optional<CarInsuranceActionCard> optActionCard = carInsuranceActionCardRepository.findById(actionCardId);
        if (optActionCard.isEmpty()) {
            logger.warn("Couldnt find the given actioncard by id: {}", actionCardId);
            return;
        }
        ActionCardText actionCardText = actionCardTextConverter.convertToActionCardText(optActionCard.get(), userGameStatus);
        
        DamageCase damageCase = new DamageCase(UUID.randomUUID().toString(), userRepository.findByFullname(userGameStatus.getUsername()), actionCardId, Insurance.CAR_INSURANCE.getName(),
                actionCardText.getDamageAmount(), actionCardText.getDamageAmountToPay(), actionCardText.getDamageAmount() - actionCardText.getDamageAmountToPay());
        damageCaseRepository.save(damageCase);
        
        logger.info("User {} has to pay {} for a damage-case in level {}", userGameStatus.getUsername(), actionCardText.getDamageAmountToPay(), userGameStatus.getLevel());
        userGameStatus.setPaidForClaims(userGameStatus.getPaidForClaims() + actionCardText.getDamageAmountToPay());
        userGameStatus.setSavedMoney(userGameStatus.getSavedMoney() + actionCardText.getSavedMoney());
        userGameStatus.addDamageCase(damageCase);
        userGameStatusRepository.save(userGameStatus);
    }
    
    /**
     * Handles the creation of actioncard for legal-protection insurance.
     * @param actionCardId
     * @param userGameStatus 
     */
    private void handleLegalProtectionInsuranceActionCard(String actionCardId, UserGameStatus userGameStatus) {
        
        Optional<LegalProtectionInsuranceActionCard> optActionCard = legalProtectionInsuranceActionCardRepository.findById(actionCardId);
        if (optActionCard.isEmpty()) {
            logger.warn("Couldnt find the given actioncard by id: {}", actionCardId);
            return;
        }
        ActionCardText actionCardText = actionCardTextConverter.convertToActionCardText(optActionCard.get(), userGameStatus);
        
        DamageCase damageCase = new DamageCase(UUID.randomUUID().toString(), userRepository.findByFullname(userGameStatus.getUsername()), actionCardId, Insurance.LEGALPROTECTION_INSURANCE.getName(),
                actionCardText.getDamageAmount(), actionCardText.getDamageAmountToPay(), actionCardText.getDamageAmount() - actionCardText.getDamageAmountToPay());
        damageCaseRepository.save(damageCase);
        
        logger.info("User {} has to pay {} for a damage-case in level {}", userGameStatus.getUsername(), actionCardText.getDamageAmountToPay(), userGameStatus.getLevel());
        userGameStatus.setPaidForClaims(userGameStatus.getPaidForClaims() + actionCardText.getDamageAmountToPay());
        userGameStatus.setSavedMoney(userGameStatus.getSavedMoney() + actionCardText.getSavedMoney());
        userGameStatus.addDamageCase(damageCase);
        userGameStatusRepository.save(userGameStatus);
    }
        
    /**
     * Handles the creation of actioncard for senior-accident insurance.
     * @param actionCardId
     * @param userGameStatus 
     */
    private void handleSeniorAccidentInsuranceActionCard(String actionCardId, UserGameStatus userGameStatus) {
        
        Optional<SeniorAccidentInsuranceActionCard> optActionCard = seniorAccidentInsuranceActionCardRepository.findById(actionCardId);
        if (optActionCard.isEmpty()) {
            logger.warn("Couldnt find the given actioncard by id: {}", actionCardId);
            return;
        }
        ActionCardText actionCardText = actionCardTextConverter.convertToActionCardText(optActionCard.get(), userGameStatus);
        
        DamageCase damageCase = new DamageCase(UUID.randomUUID().toString(), userRepository.findByFullname(userGameStatus.getUsername()), actionCardId, Insurance.SENIORACCIDENT_INSURANCE.getName(),
        actionCardText.getDamageAmount(), actionCardText.getDamageAmountToPay(), actionCardText.getDamageAmount() - actionCardText.getDamageAmountToPay());
        damageCaseRepository.save(damageCase);
        
        logger.info("User {} has to pay {} for a damage-case in level {}", userGameStatus.getUsername(), actionCardText.getDamageAmountToPay(), userGameStatus.getLevel());
        userGameStatus.setPaidForClaims(userGameStatus.getPaidForClaims() + actionCardText.getDamageAmountToPay());
        userGameStatus.setSavedMoney(userGameStatus.getSavedMoney() + actionCardText.getSavedMoney());
        userGameStatus.addDamageCase(damageCase);
        userGameStatusRepository.save(userGameStatus);
    }
    
    
    /**
     * Handles the creation of actioncard for liability insurance.
     * @param actionCardId
     * @param userGameStatus 
     */
    private void handleLiabilityInsuranceActionCard(String actionCardId, UserGameStatus userGameStatus) {
        
        Optional<LiabilityInsuranceActionCard> optActionCard = liabilityInsuranceActionCardRepository.findById(actionCardId);
        if (optActionCard.isEmpty()) {
            logger.warn("Couldnt find the given actioncard by id: {}", actionCardId);
            return;
        }
        
        ActionCardText actionCardText = actionCardTextConverter.convertToActionCardText(optActionCard.get(), userGameStatus);
        
        DamageCase damageCase = new DamageCase(UUID.randomUUID().toString(), userRepository.findByFullname(userGameStatus.getUsername()), actionCardId, Insurance.LIABILITY_INSURANCE.getName(),
                actionCardText.getDamageAmount(), actionCardText.getDamageAmountToPay(), actionCardText.getDamageAmount() - actionCardText.getDamageAmountToPay());
        damageCaseRepository.save(damageCase);
        
        
        logger.info("User {} has to pay {} for a damage-case in level {}", userGameStatus.getUsername(), actionCardText.getDamageAmountToPay(), userGameStatus.getLevel());
        userGameStatus.setPaidForClaims(userGameStatus.getPaidForClaims() + actionCardText.getDamageAmountToPay());
        userGameStatus.setSavedMoney(userGameStatus.getSavedMoney() + actionCardText.getSavedMoney());
        userGameStatus.addDamageCase(damageCase);
        userGameStatusRepository.save(userGameStatus);
    }
    
    /**
     * Handles the creation of actioncard for disability insurance.
     * @param actionCardId
     * @param userGameStatus 
     */
    private void handleDisabilityInsuranceActionCard(String actionCardId, UserGameStatus userGameStatus) {
        
        Optional<DisabilityInsuranceActionCard> optActionCard = disabilityInsuranceActionCardRepository.findById(actionCardId);
        if (optActionCard.isEmpty()) {
            logger.warn("Couldnt find the given actioncard by id: {}", actionCardId);
            return;
        }
        
        ActionCardText actionCardText = actionCardTextConverter.convertToActionCardText(optActionCard.get(), userGameStatus);
        
        DamageCase damageCase = new DamageCase(UUID.randomUUID().toString(), userRepository.findByFullname(userGameStatus.getUsername()), actionCardId, Insurance.DISABILITY_INSURANCE.getName(),
                actionCardText.getDamageAmount(), actionCardText.getDamageAmountToPay(), actionCardText.getDamageAmount() - actionCardText.getDamageAmountToPay());
        damageCaseRepository.save(damageCase);
        
        
        logger.info("User {} has to pay {} for a damage-case in level {}", userGameStatus.getUsername(), actionCardText.getDamageAmountToPay(), userGameStatus.getLevel());
        userGameStatus.setPaidForClaims(userGameStatus.getPaidForClaims() + actionCardText.getDamageAmountToPay());
        userGameStatus.setSavedMoney(userGameStatus.getSavedMoney() + actionCardText.getSavedMoney());
        userGameStatus.addDamageCase(damageCase);
        userGameStatusRepository.save(userGameStatus);
    }
    
    /**
     * Handles the creation of actioncard for household insurance.
     * @param actionCardId
     * @param userGameStatus 
     */
    private void handleHouseholdInsuranceActionCard(String actionCardId, UserGameStatus userGameStatus) {
        
        Optional<HouseholdInsuranceActionCard> optActionCard = householdInsuranceActionCardRepository.findById(actionCardId);
        if (optActionCard.isEmpty()) {
            logger.warn("Couldnt find the given actioncard by id: {}", actionCardId);
            return;
        }
        
        ActionCardText actionCardText = actionCardTextConverter.convertToActionCardText(optActionCard.get(), userGameStatus);
        
        DamageCase damageCase = new DamageCase(UUID.randomUUID().toString(), userRepository.findByFullname(userGameStatus.getUsername()), actionCardId, Insurance.HOUSEHOLD_INSURANCE.getName(),
                actionCardText.getDamageAmount(), actionCardText.getDamageAmountToPay(), actionCardText.getDamageAmount() - actionCardText.getDamageAmountToPay());
        damageCaseRepository.save(damageCase);
        
        
        logger.info("User {} has to pay {} for a damage-case in level {}", userGameStatus.getUsername(), actionCardText.getDamageAmountToPay(), userGameStatus.getLevel());
        userGameStatus.setPaidForClaims(userGameStatus.getPaidForClaims() + actionCardText.getDamageAmountToPay());
        userGameStatus.setSavedMoney(userGameStatus.getSavedMoney() + actionCardText.getSavedMoney());
        userGameStatus.addDamageCase(damageCase);
        userGameStatusRepository.save(userGameStatus);
    }
    
    /**
     * Handles the creation of actioncard for home insurance.
     * @param actionCardId
     * @param userGameStatus 
     */
    private void handleHomeInsuranceActionCard(String actionCardId, UserGameStatus userGameStatus) {
        
        Optional<HomeInsuranceActionCard> optActionCard = homeInsuranceActionCardRepository.findById(actionCardId);
        if (optActionCard.isEmpty()) {
            logger.warn("Couldnt find the given actioncard by id: {}", actionCardId);
            return;
        }
        
        ActionCardText actionCardText = actionCardTextConverter.convertToActionCardText(optActionCard.get(), userGameStatus);
        
        DamageCase damageCase = new DamageCase(UUID.randomUUID().toString(), userRepository.findByFullname(userGameStatus.getUsername()), actionCardId, Insurance.HOME_INSURANCE.getName(),
                actionCardText.getDamageAmount(), actionCardText.getDamageAmountToPay(), actionCardText.getDamageAmount() - actionCardText.getDamageAmountToPay());
        damageCaseRepository.save(damageCase);
        
        logger.info("User {} has to pay {} for a damage-case in level {}", userGameStatus.getUsername(), actionCardText.getDamageAmountToPay(), userGameStatus.getLevel());
        userGameStatus.setPaidForClaims(userGameStatus.getPaidForClaims() + actionCardText.getDamageAmountToPay());
        userGameStatus.setSavedMoney(userGameStatus.getSavedMoney() + actionCardText.getSavedMoney());
        userGameStatus.addDamageCase(damageCase);
        userGameStatusRepository.save(userGameStatus);
    }
    
    /**
     * Handles the creation of actioncard for smartphone insurance.
     * @param actionCardId
     * @param userGameStatus 
     */
    private void handleSmartphoneInsuranceActionCard(String actionCardId, UserGameStatus userGameStatus) {
        
        Optional<SmartphoneInsuranceActionCard> optActionCard = smartphoneInsuranceActionCardRepository.findById(actionCardId);
        if (optActionCard.isEmpty()) {
            logger.warn("Couldnt find the given actioncard by id: {}", actionCardId);
            return;
        }
        
        ActionCardText actionCardText = actionCardTextConverter.convertToActionCardText(optActionCard.get(), userGameStatus);
        
        DamageCase damageCase = new DamageCase(UUID.randomUUID().toString(), userRepository.findByFullname(userGameStatus.getUsername()), actionCardId, Insurance.SMARTPHONE_INSURANCE.getName(),
                actionCardText.getDamageAmount(), actionCardText.getDamageAmountToPay(), actionCardText.getDamageAmount() - actionCardText.getDamageAmountToPay());
        damageCaseRepository.save(damageCase);
        
        logger.info("User {} has to pay {} for a damage-case in level {}", userGameStatus.getUsername(), actionCardText.getDamageAmountToPay(), userGameStatus.getLevel());
        userGameStatus.setPaidForClaims(userGameStatus.getPaidForClaims() + actionCardText.getDamageAmountToPay());
        userGameStatus.setSavedMoney(userGameStatus.getSavedMoney() + actionCardText.getSavedMoney());
        userGameStatus.addDamageCase(damageCase);
        userGameStatusRepository.save(userGameStatus);
    }
    
    /**
     * Handles the creation of actioncard for termlife-insurance.
     * @param actionCardId
     * @param userGameStatus 
     */
    private void handleTermLifeInsuranceActionCard(String actionCardId, UserGameStatus userGameStatus) {
        
        Optional<TermLifeInsuranceActionCard> optActionCard = termLifeInsuranceActionCardRepository.findById(actionCardId);
        if (optActionCard.isEmpty()) {
            logger.warn("Couldnt find the given actioncard by id: {}", actionCardId);
            return;
        }
        
        ActionCardText actionCardText = actionCardTextConverter.convertToActionCardText(optActionCard.get(), userGameStatus);
        
        DamageCase damageCase = new DamageCase(UUID.randomUUID().toString(), userRepository.findByFullname(userGameStatus.getUsername()), actionCardId, Insurance.TERMLIFE_INSURANCE.getName(),
                actionCardText.getDamageAmount(), actionCardText.getDamageAmountToPay(), actionCardText.getDamageAmount() - actionCardText.getDamageAmountToPay());
        damageCaseRepository.save(damageCase);
        
        logger.info("User {} has to pay {} for a damage-case in level {}", userGameStatus.getUsername(), actionCardText.getDamageAmountToPay(), userGameStatus.getLevel());
        userGameStatus.setPaidForClaims(userGameStatus.getPaidForClaims() + actionCardText.getDamageAmountToPay());
        
        // Game over- set player to last level
        logger.info("User {} died - set em to last level.", userGameStatus.getUsername());
        userGameStatus.setLevel(Level.getSIZE());
        userGameStatus.addDamageCase(damageCase);
        userGameStatus.setSavedMoney(userGameStatus.getSavedMoney() + actionCardText.getSavedMoney());
        userGameStatusRepository.save(userGameStatus);
    }
    
    /**
     * Moves the user to next level.
     * @param username the username.
     * @param level the current level of the user.
     * @return true if successful, false if e.g. the given level doesnt equal the current level of users gamestatus in DB.
     */
    public boolean moveUserToNextLevel(String username, int level) {
        
        User user = userRepository.findByFullname(username);
        UserGameStatus userGameStatus = userGameStatusRepository.findByUsername(username);
        
        if (userGameStatus.getLevel() != level) {
            logger.warn("Couldnt move user to next level, because current game level ({}) isnt equal like the saved level in DB: {}",
                    level, userGameStatus.getLevel());
            return false;
        }
        
        // get yearly cost of all insurances and add it to the sum of all paid insurance-costs
        List<ContractedInsurance> contractedInsurances = contractedInsuranceRepository.findByUser(user);
        double yearlyCostAllInsurances = contractedInsurances.stream().mapToDouble(i -> i.getYearlyCost()).sum();
        logger.info("Adding yearly costs for all insurances of {} € to the sum of all insurance-costs: {} €", yearlyCostAllInsurances, userGameStatus.getPaidForInsurances());
        
        userGameStatus.setPaidForInsurances(userGameStatus.getPaidForInsurances() + yearlyCostAllInsurances);
        
        // remove next action-card id in usergamestatus object
        userGameStatus.setActualCardId(null);
        userGameStatus.setActualCardInsuranceName(null);
        
        int nextLevel = level + 1;
        userGameStatus.setLevel(nextLevel);
        userGameStatusRepository.save(userGameStatus);
        logger.info("Successful set user {} to next level in DB: {}", username, nextLevel);
        
        return true;
    }
    
    /**
     * Gets the insurance by the given level-id and will add it as current card into UserGameStatus object.
     * @param userGameStatus the users game status object.
     * @param level the level where the user is.
     * @return the usergame status object, enriched with the insurance-id as currently selected card.
     */
    public UserGameStatus addInsuranceToUserGameStatus(UserGameStatus userGameStatus, int level) {
        
        Insurance insurance = Insurance.findByLevel(level).get();
        
        userGameStatus.setActualCardId(String.valueOf(insurance.getId()));
        userGameStatus.setActualCardInsuranceName(insurance.getName());
        userGameStatusRepository.save(userGameStatus);
        return userGameStatus;
    }
    
    public ActionCardText getNewRandomActionCard(UserGameStatus userGameStatus) {
        
        ActionCardText returnValue = null;
        Insurance nextInsuranceType;
        String newRandomActionCardId;
        boolean pickedNewRandomCard = false;
        
        // Check if user has already picked a new card and needs to confirm that
        if (StringUtils.hasText(userGameStatus.getActualCardId()) && StringUtils.hasText(userGameStatus.getActualCardInsuranceName())) {
            nextInsuranceType = Insurance.findByName(userGameStatus.getActualCardInsuranceName()).get();
            newRandomActionCardId = userGameStatus.getActualCardId();
        } else {
            InsuranceWithType newRandomActionCard = cardPicker.getNewRandomActionCard(userGameStatus);
            newRandomActionCardId = newRandomActionCard.getId();
            nextInsuranceType = newRandomActionCard.getInsurance();
            pickedNewRandomCard = true;
        }
        
        switch (nextInsuranceType) {
            case SMARTPHONE_INSURANCE:
                Optional<SmartphoneInsuranceActionCard> optSmartphoneInsuranceActionCard = smartphoneInsuranceActionCardRepository.findById(newRandomActionCardId);
                returnValue = actionCardTextConverter.convertToActionCardText(optSmartphoneInsuranceActionCard.get(), userGameStatus);
                break;
            case CAR_INSURANCE:
                Optional<CarInsuranceActionCard> optCarInsuranceActionCard = carInsuranceActionCardRepository.findById(newRandomActionCardId);
                returnValue = actionCardTextConverter.convertToActionCardText(optCarInsuranceActionCard.get(), userGameStatus);
                break;
            case DISABILITY_INSURANCE:
                Optional<DisabilityInsuranceActionCard> optDisabilityInsuranceActionCard = disabilityInsuranceActionCardRepository.findById(newRandomActionCardId);
                returnValue = actionCardTextConverter.convertToActionCardText(optDisabilityInsuranceActionCard.get(), userGameStatus);
                break;
            case HOUSEHOLD_INSURANCE:
                Optional<HouseholdInsuranceActionCard> optHouseholdInsuranceActionCard = householdInsuranceActionCardRepository.findById(newRandomActionCardId);
                returnValue = actionCardTextConverter.convertToActionCardText(optHouseholdInsuranceActionCard.get(), userGameStatus);
                break;
            case LIABILITY_INSURANCE:
                Optional<LiabilityInsuranceActionCard> optLiabilityInsuranceActionCard = liabilityInsuranceActionCardRepository.findById(newRandomActionCardId);
                returnValue = actionCardTextConverter.convertToActionCardText(optLiabilityInsuranceActionCard.get(), userGameStatus);
                break;
            case HOME_INSURANCE:
                Optional<HomeInsuranceActionCard> optHomeInsuranceActionCard = homeInsuranceActionCardRepository.findById(newRandomActionCardId);
                returnValue = actionCardTextConverter.convertToActionCardText(optHomeInsuranceActionCard.get(), userGameStatus);
                break;
            case TERMLIFE_INSURANCE:
                Optional<TermLifeInsuranceActionCard> optTermLifeInsuranceActionCard = termLifeInsuranceActionCardRepository.findById(newRandomActionCardId);
                returnValue = actionCardTextConverter.convertToActionCardText(optTermLifeInsuranceActionCard.get(), userGameStatus);
                break;
            case LEGALPROTECTION_INSURANCE:
                Optional<LegalProtectionInsuranceActionCard> optLegalProtectionInsuranceActionCard = legalProtectionInsuranceActionCardRepository.findById(newRandomActionCardId);
                returnValue = actionCardTextConverter.convertToActionCardText(optLegalProtectionInsuranceActionCard.get(), userGameStatus);
                break;
            case SENIORACCIDENT_INSURANCE:
                Optional<SeniorAccidentInsuranceActionCard> optSeniorAccidentInsuranceActionCard = seniorAccidentInsuranceActionCardRepository.findById(newRandomActionCardId);
                actionCardTextConverter.convertToActionCardText(optSeniorAccidentInsuranceActionCard.get(), userGameStatus);
                break;
        }
        
        // Save the new draw actioncard in usergamestatus object
        if (pickedNewRandomCard) {
            userGameStatus.setActualCardId(returnValue.getActionCardId());
            userGameStatus.setActualCardInsuranceName(returnValue.getInsuranceType());
            userGameStatusRepository.save(userGameStatus);
        }
        
        return returnValue;
    }

    public String getUsernameFromSession(HttpSession session) {
        return (String) session.getAttribute(WebSecurityConfig.SESSION_FIELD_USERNAME);
    }
    

}
