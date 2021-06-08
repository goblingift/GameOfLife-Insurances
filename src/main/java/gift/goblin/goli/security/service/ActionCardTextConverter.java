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
import gift.goblin.goli.database.repository.UserRepository;
import gift.goblin.goli.dto.ActionCardText;
import gift.goblin.goli.enumerations.Insurance;
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

    public ActionCardText convertToActionCardText(CarInsuranceActionCard carInsuranceActionCard, UserGameStatus userGameStatus) {

        String damageCaseAdditionalDescription = "";
        boolean enoughInsurance = false;
        double damageAmountToPay = carInsuranceActionCard.getDamageAmount();

        // Get the insurance contract of the user
        User user = userRepository.findByFullname(userGameStatus.getUsername());
        Optional<ContractedInsurance> optInsurance = contractedInsuranceRepository.findByUserAndInsuranceId(user, Insurance.CAR_INSURANCE.getId());
        if (optInsurance.isPresent()) {
            ContractedInsurance contractedInsurance = optInsurance.get();
            int selectedChoice = contractedInsurance.getSelectedChoice();

            if (carInsuranceActionCard.isCoveredByHaftpflicht() && selectedChoice >= 1) {
                enoughInsurance = true;
            } else if (carInsuranceActionCard.isCoveredByTeilkasko() && selectedChoice >= 2) {
                enoughInsurance = true;
            } else if (carInsuranceActionCard.isCoveredByVollkasko() && selectedChoice >= 3) {
                enoughInsurance = true;
            }
        }

        if (enoughInsurance) {
            damageAmountToPay = 0.00;
            damageCaseAdditionalDescription = messageSource.getMessage("decision.insurance.result.fullrefund.level.1", null, Locale.GERMANY);
        } else {
            damageCaseAdditionalDescription = messageSource.getMessage("decision.insurance.result.norefund.level.1", null, Locale.GERMANY);
        }

        return new ActionCardText(Insurance.CAR_INSURANCE.getName(), carInsuranceActionCard.getText(), damageCaseAdditionalDescription, carInsuranceActionCard.getDamageAmount(), damageAmountToPay);
    }

    public ActionCardText convertToActionCardText(DisabilityInsuranceActionCard disabilityInsuranceActionCard, UserGameStatus userGameStatus) {

        String damageCaseAdditionalDescription = "";
        double damageAmountToPay = disabilityInsuranceActionCard.getDamageAmount();

        // Get the insurance contract of the user
        User user = userRepository.findByFullname(userGameStatus.getUsername());
        Optional<ContractedInsurance> optInsurance = contractedInsuranceRepository.findByUserAndInsuranceId(user, Insurance.CAR_INSURANCE.getId());
        if (optInsurance.isPresent()) {
            ContractedInsurance contractedInsurance = optInsurance.get();
            int selectedChoice = contractedInsurance.getSelectedChoice();
            // partialrefund

            if (selectedChoice == 1) {
                damageAmountToPay = disabilityInsuranceActionCard.getDamageAmount();
                damageCaseAdditionalDescription = messageSource.getMessage("decision.insurance.result.norefund.level.2", null, Locale.GERMANY);
            } else if (selectedChoice == 2) {
                if (disabilityInsuranceActionCard.getDamageAmount() >= 300) {
                    damageAmountToPay = 300;
                } else {
                    damageAmountToPay = disabilityInsuranceActionCard.getDamageAmount();
                }
                damageCaseAdditionalDescription = messageSource.getMessage("decision.insurance.result.partialrefund.level.2", null, Locale.GERMANY);
            } else if (selectedChoice == 3) {
                damageAmountToPay = 0.00;
                damageCaseAdditionalDescription = messageSource.getMessage("decision.insurance.result.fullrefund.level.2", null, Locale.GERMANY);
            }
        }
        
        return new ActionCardText(Insurance.DISABILITY_INSURANCE.getName(), disabilityInsuranceActionCard.getText(), damageCaseAdditionalDescription, disabilityInsuranceActionCard.getDamageAmount(), damageAmountToPay);
    }

}
