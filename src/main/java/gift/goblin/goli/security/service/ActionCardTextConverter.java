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
import gift.goblin.goli.database.model.actioncards.HouseholdInsuranceActionCard;
import gift.goblin.goli.database.model.actioncards.LiabilityInsuranceActionCard;
import gift.goblin.goli.database.repository.ContractedInsuranceRepository;
import gift.goblin.goli.database.repository.UserRepository;
import gift.goblin.goli.dto.ActionCardText;
import gift.goblin.goli.enumerations.Insurance;
import gift.goblin.goli.enumerations.Level;
import java.util.Locale;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

/**
 * Converts several objects int o ActionCardText objects.
 *
 * @author andre
 */
@Service
public class ActionCardTextConverter {

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private ContractedInsuranceRepository contractedInsuranceRepository;

    @Autowired
    private UserRepository userRepository;

    public ActionCardText convertToActionCardText(CarInsuranceActionCard actionCard, UserGameStatus userGameStatus) {

        String damageCaseAdditionalDescription = "";
        boolean enoughInsurance = false;
        double damageAmountToPay = actionCard.getDamageAmount();

        // Get the insurance contract of the user
        User user = userRepository.findByFullname(userGameStatus.getUsername());
        Optional<ContractedInsurance> optInsurance = contractedInsuranceRepository.findByUserAndInsuranceId(user, Insurance.CAR_INSURANCE.getId());
        if (optInsurance.isPresent()) {
            ContractedInsurance contractedInsurance = optInsurance.get();
            int selectedChoice = contractedInsurance.getSelectedChoice();

            if (actionCard.isCoveredByHaftpflicht() && selectedChoice >= 1) {
                enoughInsurance = true;
            } else if (actionCard.isCoveredByTeilkasko() && selectedChoice >= 2) {
                enoughInsurance = true;
            } else if (actionCard.isCoveredByVollkasko() && selectedChoice >= 3) {
                enoughInsurance = true;
            }
        }

        if (enoughInsurance) {
            damageAmountToPay = 0.00;
            damageCaseAdditionalDescription = messageSource.getMessage("decision.insurance.result.fullrefund.level.1", null, Locale.GERMANY);
        } else {
            damageCaseAdditionalDescription = messageSource.getMessage("decision.insurance.result.norefund.level.1", null, Locale.GERMANY);
        }

        return new ActionCardText(Insurance.CAR_INSURANCE.getName(), actionCard.getId(), actionCard.getText(),
                damageCaseAdditionalDescription, actionCard.getDamageAmount(), damageAmountToPay);
    }

    
    public ActionCardText convertToActionCardText(LiabilityInsuranceActionCard actionCard, UserGameStatus userGameStatus) {

        String damageCaseAdditionalDescription = "";
        double damageAmountToPay = actionCard.getDamageAmount();

        // Get the insurance contract of the user
        User user = userRepository.findByFullname(userGameStatus.getUsername());
        Optional<ContractedInsurance> optInsurance = contractedInsuranceRepository.findByUserAndInsuranceId(user, Insurance.LIABILITY_INSURANCE.getId());
        if (optInsurance.isPresent()) {
            ContractedInsurance contractedInsurance = optInsurance.get();
            int selectedChoice = contractedInsurance.getSelectedChoice();

            if (selectedChoice == 1) {
                damageAmountToPay = actionCard.getDamageAmount();
                damageCaseAdditionalDescription = messageSource.getMessage("decision.insurance.result.norefund.level.2", null, Locale.GERMANY);
            } else if (selectedChoice == 2) {
                if (actionCard.getDamageAmount() >= 300) {
                    damageAmountToPay = 300;
                } else {
                    damageAmountToPay = actionCard.getDamageAmount();
                }
                damageCaseAdditionalDescription = messageSource.getMessage("decision.insurance.result.partialrefund.level.2", null, Locale.GERMANY);
            } else if (selectedChoice == 3) {
                damageAmountToPay = 0.00;
                damageCaseAdditionalDescription = messageSource.getMessage("decision.insurance.result.fullrefund.level.2", null, Locale.GERMANY);
            }
        }

        return new ActionCardText(Insurance.LIABILITY_INSURANCE.getName(), actionCard.getId(), actionCard.getText(),
                damageCaseAdditionalDescription, actionCard.getDamageAmount(), damageAmountToPay);
    }
    
    public ActionCardText convertToActionCardText(DisabilityInsuranceActionCard actionCard, UserGameStatus userGameStatus) {

        String damageCaseAdditionalDescription = "";
        double damageAmountToPay = 0.00;
        double averageYearlyIncomeGermany2020 = 47_700.00;
        int yearsWithLessMoney = (int) Math.round(actionCard.getDamageAmount());
        int yearsToLive = Level.getSIZE() - userGameStatus.getLevel();
        // player can only get as many years less money as they live
        if (yearsWithLessMoney > yearsToLive) {
            // player will get less money for all remaining years
            yearsWithLessMoney = yearsToLive;
        }
            
        // Get the insurance contract of the user
        User user = userRepository.findByFullname(userGameStatus.getUsername());
        Optional<ContractedInsurance> optInsurance = contractedInsuranceRepository.findByUserAndInsuranceId(user, Insurance.DISABILITY_INSURANCE.getId());
        if (optInsurance.isPresent()) {
            ContractedInsurance contractedInsurance = optInsurance.get();
            int selectedChoice = contractedInsurance.getSelectedChoice();
            
            damageCaseAdditionalDescription = messageSource.getMessage("decision.insurance.result.introduction.level.3", null, Locale.GERMANY);
            damageCaseAdditionalDescription = damageCaseAdditionalDescription.replace("{years}", String.valueOf(yearsWithLessMoney));
            
            switch (selectedChoice) {
                case 1:
                    damageAmountToPay = yearsWithLessMoney * (averageYearlyIncomeGermany2020 * 0.6);
                    damageCaseAdditionalDescription.concat(messageSource.getMessage("decision.insurance.result.norefund.level.3", null, Locale.GERMANY));
                    break;
                case 2:
                    damageAmountToPay = yearsWithLessMoney * (averageYearlyIncomeGermany2020 * 0.4);
                    damageCaseAdditionalDescription.concat(messageSource.getMessage("decision.insurance.result.partialrefund.level.3", null, Locale.GERMANY));
                    break;
                case 3:
                    damageAmountToPay = yearsWithLessMoney * (averageYearlyIncomeGermany2020 * 0.1);
                    damageCaseAdditionalDescription.concat(messageSource.getMessage("decision.insurance.result.fullrefund.level.3", null, Locale.GERMANY));
                    break;
                default:
                    break;
            }
        }

        return new ActionCardText(Insurance.DISABILITY_INSURANCE.getName(), actionCard.getId(), actionCard.getText(),
                damageCaseAdditionalDescription, (yearsWithLessMoney * averageYearlyIncomeGermany2020) * 0.6, damageAmountToPay);
    }
    
    
    public ActionCardText convertToActionCardText(HouseholdInsuranceActionCard actionCard, UserGameStatus userGameStatus) {

        String damageCaseAdditionalDescription = "";
        boolean enoughInsurance = false;
        double damageAmountToPay = actionCard.getDamageAmount();

        // Get the insurance contract of the user
        User user = userRepository.findByFullname(userGameStatus.getUsername());
        Optional<ContractedInsurance> optInsurance = contractedInsuranceRepository.findByUserAndInsuranceId(user, Insurance.HOUSEHOLD_INSURANCE.getId());
        if (optInsurance.isPresent()) {
            ContractedInsurance contractedInsurance = optInsurance.get();
            int selectedChoice = contractedInsurance.getSelectedChoice();

            if (!actionCard.isCoveredByAddition() && selectedChoice >= 2) {
                enoughInsurance = true;
                damageAmountToPay = 0.00;
                damageCaseAdditionalDescription = messageSource.getMessage("decision.insurance.result.fullrefund.level.4", null, Locale.GERMANY);
            } else if (actionCard.isCoveredByAddition() && selectedChoice >= 3) {
                enoughInsurance = true;
                damageAmountToPay = 0.00;
                damageCaseAdditionalDescription = messageSource.getMessage("decision.insurance.result.fullrefund.level.4", null, Locale.GERMANY);
            } else if (actionCard.isCoveredByAddition() && selectedChoice == 2) {
                enoughInsurance = false;
                damageCaseAdditionalDescription = messageSource.getMessage("decision.insurance.result.additionrequired.level.4", null, Locale.GERMANY);
            } else {
                enoughInsurance = false;
                damageCaseAdditionalDescription = messageSource.getMessage("decision.insurance.result.norefund.level.1", null, Locale.GERMANY);
            }
        }
        
        return new ActionCardText(Insurance.HOUSEHOLD_INSURANCE.getName(), actionCard.getId(), actionCard.getText(),
                damageCaseAdditionalDescription, actionCard.getDamageAmount(), damageAmountToPay);
    }

}
