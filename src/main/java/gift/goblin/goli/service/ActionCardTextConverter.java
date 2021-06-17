/*
 * Copyright (C) 2021 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.goli.service;

import gift.goblin.goli.database.model.ContractedInsurance;
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
            damageCaseAdditionalDescription = messageSource.getMessage("decision.insurance.result.fullrefund.id." + Insurance.CAR_INSURANCE.getId(), null, Locale.GERMANY);
        } else {
            damageCaseAdditionalDescription = messageSource.getMessage("decision.insurance.result.norefund.id." + Insurance.CAR_INSURANCE.getId(), null, Locale.GERMANY);
        }

        return new ActionCardText(Insurance.CAR_INSURANCE.getName(), actionCard.getId(), actionCard.getText(),
                damageCaseAdditionalDescription, actionCard.getDamageAmount(), damageAmountToPay, actionCard.getDamageAmount() - damageAmountToPay);
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
                damageCaseAdditionalDescription = messageSource.getMessage("decision.insurance.result.norefund.id." + Insurance.LIABILITY_INSURANCE.getId(), null, Locale.GERMANY);
            } else if (selectedChoice == 2) {
                if (actionCard.getDamageAmount() >= 300) {
                    damageAmountToPay = 300;
                } else {
                    damageAmountToPay = actionCard.getDamageAmount();
                }
                damageCaseAdditionalDescription = messageSource.getMessage("decision.insurance.result.partialrefund.id." + Insurance.LIABILITY_INSURANCE.getId(), null, Locale.GERMANY);
            } else if (selectedChoice == 3) {
                damageAmountToPay = 0.00;
                damageCaseAdditionalDescription = messageSource.getMessage("decision.insurance.result.fullrefund.id." + Insurance.LIABILITY_INSURANCE.getId(), null, Locale.GERMANY);
            }
        }

        return new ActionCardText(Insurance.LIABILITY_INSURANCE.getName(), actionCard.getId(), actionCard.getText(),
                damageCaseAdditionalDescription, actionCard.getDamageAmount(), damageAmountToPay, actionCard.getDamageAmount() - damageAmountToPay);
    }
    
    public ActionCardText convertToActionCardText(SeniorAccidentInsuranceActionCard actionCard, UserGameStatus userGameStatus) {

        String damageCaseAdditionalDescription = "";
        double damageAmountToPay = actionCard.getDamageAmount();

        // Get the insurance contract of the user
        User user = userRepository.findByFullname(userGameStatus.getUsername());
        Optional<ContractedInsurance> optInsurance = contractedInsuranceRepository.findByUserAndInsuranceId(user, Insurance.SENIORACCIDENT_INSURANCE.getId());
        if (optInsurance.isPresent()) {
            ContractedInsurance contractedInsurance = optInsurance.get();
            int selectedChoice = contractedInsurance.getSelectedChoice();

            if (selectedChoice == 1) {
                damageAmountToPay = actionCard.getDamageAmount();
                damageCaseAdditionalDescription = messageSource.getMessage("decision.insurance.result.norefund.id." + Insurance.SENIORACCIDENT_INSURANCE.getId(), null, Locale.GERMANY);
            } else if (selectedChoice == 2) {
                if (actionCard.getDamageAmount() > 40_000) {
                    damageAmountToPay = actionCard.getDamageAmount() - 40_000;
                } else {
                    damageAmountToPay = 0.00;
                }
                damageCaseAdditionalDescription = messageSource.getMessage("decision.insurance.result.partialrefund.id." + Insurance.SENIORACCIDENT_INSURANCE.getId(), null, Locale.GERMANY);
            } else if (selectedChoice == 3) {
                if (actionCard.getDamageAmount() > 75_000) {
                    damageAmountToPay = actionCard.getDamageAmount() - 75_000;
                } else {
                    damageAmountToPay = 0.00;
                }
                damageCaseAdditionalDescription = messageSource.getMessage("decision.insurance.result.fullrefund.id." + Insurance.SENIORACCIDENT_INSURANCE.getId(), null, Locale.GERMANY);
            }
        }

        return new ActionCardText(Insurance.SENIORACCIDENT_INSURANCE.getName(), actionCard.getId(), actionCard.getText(),
                damageCaseAdditionalDescription, actionCard.getDamageAmount(), damageAmountToPay, actionCard.getDamageAmount() - damageAmountToPay);
    }
    
    
    public ActionCardText convertToActionCardText(LegalProtectionInsuranceActionCard actionCard, UserGameStatus userGameStatus) {

        String damageCaseAdditionalDescription = "";
        boolean enoughInsurance = false;
        double damageAmountToPay = actionCard.getDamageAmount();

        // Get the insurance contract of the user
        User user = userRepository.findByFullname(userGameStatus.getUsername());
        Optional<ContractedInsurance> optInsurance = contractedInsuranceRepository.findByUserAndInsuranceId(user, Insurance.LEGALPROTECTION_INSURANCE.getId());
        if (optInsurance.isPresent()) {
            ContractedInsurance contractedInsurance = optInsurance.get();
            int selectedChoice = contractedInsurance.getSelectedChoice();

            if (actionCard.isCoveredByPrivateInsurance() && selectedChoice >= 2) {
                enoughInsurance = true;
            } else if (actionCard.isCoveredByTrafficInsurance() && selectedChoice >= 3) {
                enoughInsurance = true;
            } else if (actionCard.isCoveredByJobInsurance() && selectedChoice >= 4) {
                enoughInsurance = true;
            }
        }

        if (enoughInsurance) {
            damageAmountToPay = 0.00;
            damageCaseAdditionalDescription = messageSource.getMessage("decision.insurance.result.fullrefund.id." + Insurance.LEGALPROTECTION_INSURANCE.getId(), null, Locale.GERMANY);
        } else {
            damageCaseAdditionalDescription = messageSource.getMessage("decision.insurance.result.norefund.id." + Insurance.LEGALPROTECTION_INSURANCE.getId(), null, Locale.GERMANY);
        }

        return new ActionCardText(Insurance.LEGALPROTECTION_INSURANCE.getName(), actionCard.getId(), actionCard.getText(),
                damageCaseAdditionalDescription, actionCard.getDamageAmount(), damageAmountToPay, actionCard.getDamageAmount() - damageAmountToPay);
    }
    
    public ActionCardText convertToActionCardText(DisabilityInsuranceActionCard actionCard, UserGameStatus userGameStatus) {

        String damageCaseAdditionalDescription = "";
        double damageAmountToPay = 0.00;
        double averageYearlyIncomeGermany2020 = 47_700.00;
        int yearsWithLessMoney = (int)actionCard.getDamageAmount();
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
            
            damageCaseAdditionalDescription = messageSource.getMessage("decision.insurance.result.introduction.id." + Insurance.DISABILITY_INSURANCE.getId(), null, Locale.GERMANY);
            damageCaseAdditionalDescription = damageCaseAdditionalDescription.replace("{years}", String.valueOf(yearsWithLessMoney));
            
            switch (selectedChoice) {
                case 1:
                    damageAmountToPay = yearsWithLessMoney * (averageYearlyIncomeGermany2020 * 0.6);
                    damageCaseAdditionalDescription = damageCaseAdditionalDescription.concat(messageSource.getMessage("decision.insurance.result.norefund.id." + Insurance.DISABILITY_INSURANCE.getId(), null, Locale.GERMANY));
                    break;
                case 2:
                    damageAmountToPay = yearsWithLessMoney * (averageYearlyIncomeGermany2020 * 0.4);
                    damageCaseAdditionalDescription = damageCaseAdditionalDescription.concat(messageSource.getMessage("decision.insurance.result.partialrefund.id." + Insurance.DISABILITY_INSURANCE.getId(), null, Locale.GERMANY));
                    break;
                case 3:
                    damageAmountToPay = yearsWithLessMoney * (averageYearlyIncomeGermany2020 * 0.1);
                    damageCaseAdditionalDescription = damageCaseAdditionalDescription.concat(messageSource.getMessage("decision.insurance.result.fullrefund.id." + Insurance.DISABILITY_INSURANCE.getId(), null, Locale.GERMANY));
                    break;
                default:
                    break;
            }
        }
        double worstCaseDamageAmountSum = (yearsWithLessMoney * averageYearlyIncomeGermany2020) * 0.6;
        ActionCardText actionCardText = new ActionCardText(Insurance.DISABILITY_INSURANCE.getName(), actionCard.getId(), actionCard.getText(),
                damageCaseAdditionalDescription, worstCaseDamageAmountSum, damageAmountToPay, worstCaseDamageAmountSum - damageAmountToPay);
        actionCardText.setDifferentTextDamageAmountSum(messageSource.getMessage("decision.insurance.result.label.damagesum.id." + Insurance.DISABILITY_INSURANCE.getId(), null, Locale.GERMANY));
        actionCardText.setDifferentTextDamageAmount(messageSource.getMessage("decision.insurance.result.label.damage.id." + Insurance.DISABILITY_INSURANCE.getId(), null, Locale.GERMANY));
        return actionCardText;
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
                damageCaseAdditionalDescription = messageSource.getMessage("decision.insurance.result.fullrefund.id." + Insurance.HOUSEHOLD_INSURANCE.getId(), null, Locale.GERMANY);
            } else if (actionCard.isCoveredByAddition() && selectedChoice >= 3) {
                enoughInsurance = true;
                damageAmountToPay = 0.00;
                damageCaseAdditionalDescription = messageSource.getMessage("decision.insurance.result.fullrefund.id." + Insurance.HOUSEHOLD_INSURANCE.getId(), null, Locale.GERMANY);
            } else if (actionCard.isCoveredByAddition() && selectedChoice == 2) {
                enoughInsurance = false;
                damageCaseAdditionalDescription = messageSource.getMessage("decision.insurance.result.additionrequired.id." + Insurance.HOUSEHOLD_INSURANCE.getId(), null, Locale.GERMANY);
            } else {
                enoughInsurance = false;
                damageCaseAdditionalDescription = messageSource.getMessage("decision.insurance.result.norefund.id." + Insurance.HOUSEHOLD_INSURANCE.getId(), null, Locale.GERMANY);
            }
        }
        
        return new ActionCardText(Insurance.HOUSEHOLD_INSURANCE.getName(), actionCard.getId(), actionCard.getText(),
                damageCaseAdditionalDescription, actionCard.getDamageAmount(), damageAmountToPay, actionCard.getDamageAmount() - damageAmountToPay);
    }
    
    
    public ActionCardText convertToActionCardText(HomeInsuranceActionCard actionCard, UserGameStatus userGameStatus) {

        String damageCaseAdditionalDescription = "";
        boolean enoughInsurance = false;
        double damageAmountToPay = actionCard.getDamageAmount();

        // Get the insurance contract of the user
        User user = userRepository.findByFullname(userGameStatus.getUsername());
        Optional<ContractedInsurance> optInsurance = contractedInsuranceRepository.findByUserAndInsuranceId(user, Insurance.HOME_INSURANCE.getId());
        if (optInsurance.isPresent()) {
            ContractedInsurance contractedInsurance = optInsurance.get();
            int selectedChoice = contractedInsurance.getSelectedChoice();

            if (!actionCard.isCoveredByAddition() && selectedChoice >= 2) {
                enoughInsurance = true;
                damageAmountToPay = 0.00;
                damageCaseAdditionalDescription = messageSource.getMessage("decision.insurance.result.fullrefund.id." + Insurance.HOME_INSURANCE.getId(), null, Locale.GERMANY);
            } else if (actionCard.isCoveredByAddition() && selectedChoice >= 3) {
                enoughInsurance = true;
                damageAmountToPay = 0.00;
                damageCaseAdditionalDescription = messageSource.getMessage("decision.insurance.result.fullrefund.id." + Insurance.HOME_INSURANCE.getId(), null, Locale.GERMANY);
            } else if (actionCard.isCoveredByAddition() && selectedChoice == 2) {
                enoughInsurance = false;
                damageCaseAdditionalDescription = messageSource.getMessage("decision.insurance.result.additionrequired.id." + + Insurance.HOME_INSURANCE.getId(), null, Locale.GERMANY);
            } else {
                enoughInsurance = false;
                damageCaseAdditionalDescription = messageSource.getMessage("decision.insurance.result.norefund.id." + Insurance.HOME_INSURANCE.getId(), null, Locale.GERMANY);
            }
        }
        
        return new ActionCardText(Insurance.HOME_INSURANCE.getName(), actionCard.getId(), actionCard.getText(),
                damageCaseAdditionalDescription, actionCard.getDamageAmount(), damageAmountToPay, actionCard.getDamageAmount() - damageAmountToPay);
    }
    
        public ActionCardText convertToActionCardText(TermLifeInsuranceActionCard actionCard, UserGameStatus userGameStatus) {

        String damageCaseAdditionalDescription = "";
        double compensationPayment = 0.00;

        // Get the insurance contract of the user
        User user = userRepository.findByFullname(userGameStatus.getUsername());
        Optional<ContractedInsurance> optInsurance = contractedInsuranceRepository.findByUserAndInsuranceId(user, Insurance.TERMLIFE_INSURANCE.getId());
        if (optInsurance.isPresent()) {
            ContractedInsurance contractedInsurance = optInsurance.get();
            int selectedChoice = contractedInsurance.getSelectedChoice();

            switch (selectedChoice) {
                    case 1:
                        compensationPayment = 0.00;
                        damageCaseAdditionalDescription = messageSource.getMessage("decision.insurance.result.norefund.id." + Insurance.TERMLIFE_INSURANCE.getId(), null, Locale.GERMANY);
                        break;
                    case 2:
                        compensationPayment = 250_000;
                        damageCaseAdditionalDescription = messageSource.getMessage("decision.insurance.result.partialrefund.id." + Insurance.TERMLIFE_INSURANCE.getId(), null, Locale.GERMANY);
                        break;
                    case 3:
                        compensationPayment = 500_000;
                        damageCaseAdditionalDescription = messageSource.getMessage("decision.insurance.result.fullrefund.id." + Insurance.TERMLIFE_INSURANCE.getId(), null, Locale.GERMANY);
                        break;
            }
        }
        
        ActionCardText actionCardText = new ActionCardText(Insurance.TERMLIFE_INSURANCE.getName(), actionCard.getId(), actionCard.getText(),
                damageCaseAdditionalDescription, 0.00, 0.00, compensationPayment);
        actionCardText.setDifferentTextDamageAmount(messageSource.getMessage("decision.insurance.result.label.damage.id." + Insurance.TERMLIFE_INSURANCE.getId(), null, Locale.GERMANY));
        return actionCardText;
    }
    
    public ActionCardText convertToActionCardText(SmartphoneInsuranceActionCard actionCard, UserGameStatus userGameStatus) {

        String damageCaseAdditionalDescription = "";
        boolean enoughInsurance = false;
        double damageAmountToPay = actionCard.getDamageAmount();

        // Get the insurance contract of the user
        User user = userRepository.findByFullname(userGameStatus.getUsername());
        Optional<ContractedInsurance> optInsurance = contractedInsuranceRepository.findByUserAndInsuranceId(user, Insurance.SMARTPHONE_INSURANCE.getId());
        if (optInsurance.isPresent()) {
            ContractedInsurance contractedInsurance = optInsurance.get();
            int selectedChoice = contractedInsurance.getSelectedChoice();

            if (!actionCard.isCoveredByAddition() && selectedChoice >= 2) {
                enoughInsurance = true;
                damageAmountToPay = 0.00;
                damageCaseAdditionalDescription = messageSource.getMessage("decision.insurance.result.fullrefund.id." + Insurance.SMARTPHONE_INSURANCE.getId(), null, Locale.GERMANY);
            } else if (actionCard.isCoveredByAddition() && selectedChoice >= 3) {
                enoughInsurance = true;
                damageAmountToPay = 0.00;
                damageCaseAdditionalDescription = messageSource.getMessage("decision.insurance.result.fullrefund.id." + Insurance.SMARTPHONE_INSURANCE.getId(), null, Locale.GERMANY);
            } else if (actionCard.isCoveredByAddition() && selectedChoice == 2) {
                enoughInsurance = false;
                damageCaseAdditionalDescription = messageSource.getMessage("decision.insurance.result.additionrequired.id." + Insurance.SMARTPHONE_INSURANCE.getId(), null, Locale.GERMANY);
            } else {
                enoughInsurance = false;
                damageCaseAdditionalDescription = messageSource.getMessage("decision.insurance.result.norefund.id." + Insurance.SMARTPHONE_INSURANCE.getId(), null, Locale.GERMANY);
            }
        }
        
        return new ActionCardText(Insurance.SMARTPHONE_INSURANCE.getName(), actionCard.getId(), actionCard.getText(),
                damageCaseAdditionalDescription, actionCard.getDamageAmount(), damageAmountToPay, actionCard.getDamageAmount() - damageAmountToPay);
    }
    

}
