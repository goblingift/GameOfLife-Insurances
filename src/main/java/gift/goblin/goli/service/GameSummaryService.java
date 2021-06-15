/*
 * Copyright (C) 2021 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.goli.service;

import gift.goblin.goli.database.model.ContractedInsurance;
import gift.goblin.goli.database.model.DamageCase;
import gift.goblin.goli.database.model.User;
import gift.goblin.goli.database.model.UserGameStatus;
import gift.goblin.goli.database.repository.ContractedInsuranceRepository;
import gift.goblin.goli.database.repository.DamageCaseRepository;
import gift.goblin.goli.database.repository.UserRepository;
import gift.goblin.goli.dto.GameOverSummary;
import gift.goblin.goli.dto.InsuranceSummary;
import gift.goblin.goli.enumerations.Insurance;
import gift.goblin.goli.enumerations.Level;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

/**
 * Contains logic to generate summary statistics of the game.
 *
 * @author andre
 */
@Service
public class GameSummaryService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    GameCardService gameCardService;

    @Autowired
    ContractedInsuranceRepository contractedInsuranceRepository;

    @Autowired
    DamageCaseRepository damageCaseRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    private MessageSource messageSource;

    public GameOverSummary generateGameSummary(String username) {

        UserGameStatus userGameStatus = gameCardService.getUserGameStatus(username);
        User user = userRepository.findByFullname(username);

        GameOverSummary gameOverSummary = new GameOverSummary();
        gameOverSummary.setPaidCostsSum(userGameStatus.getPaidForInsurances());
        gameOverSummary.setPaidDamageCostsSum(userGameStatus.getPaidForClaims());
        double savedMoney = 0.00;
        List<InsuranceSummary> insuranceSummaries = new ArrayList<>();

        InsuranceSummary insuranceSummary = generateInsuranceSummary(userGameStatus, user, Insurance.CAR_INSURANCE);
        insuranceSummaries.add(insuranceSummary);
        savedMoney += insuranceSummary.getSavedMoney();

        insuranceSummary = generateInsuranceSummary(userGameStatus, user, Insurance.DISABILITY_INSURANCE);
        insuranceSummaries.add(insuranceSummary);
        savedMoney += insuranceSummary.getSavedMoney();

        insuranceSummary = generateInsuranceSummary(userGameStatus, user, Insurance.HOME_INSURANCE);
        insuranceSummaries.add(insuranceSummary);
        savedMoney += insuranceSummary.getSavedMoney();

        insuranceSummary = generateInsuranceSummary(userGameStatus, user, Insurance.HOUSEHOLD_INSURANCE);
        insuranceSummaries.add(insuranceSummary);
        savedMoney += insuranceSummary.getSavedMoney();

        insuranceSummary = generateInsuranceSummary(userGameStatus, user, Insurance.LEGALPROTECTION_INSURANCE);
        insuranceSummaries.add(insuranceSummary);
        savedMoney += insuranceSummary.getSavedMoney();

        insuranceSummary = generateInsuranceSummary(userGameStatus, user, Insurance.LIABILITY_INSURANCE);
        insuranceSummaries.add(insuranceSummary);
        savedMoney += insuranceSummary.getSavedMoney();

        insuranceSummary = generateInsuranceSummary(userGameStatus, user, Insurance.SENIORACCIDENT_INSURANCE);
        insuranceSummaries.add(insuranceSummary);
        savedMoney += insuranceSummary.getSavedMoney();

        insuranceSummary = generateInsuranceSummary(userGameStatus, user, Insurance.SMARTPHONE_INSURANCE);
        insuranceSummaries.add(insuranceSummary);
        savedMoney += insuranceSummary.getSavedMoney();

        insuranceSummary = generateInsuranceSummary(userGameStatus, user, Insurance.TERMLIFE_INSURANCE);
        insuranceSummaries.add(insuranceSummary);
        savedMoney += insuranceSummary.getSavedMoney();

        gameOverSummary.setSavedMoneySum(savedMoney);
        gameOverSummary.setInsuranceSummaries(insuranceSummaries);
        return gameOverSummary;
    }

    private double calculatePaidClaims(User user, String insuranceName) {
        double returnValue = 0.00;
        List<DamageCase> damageCases = damageCaseRepository.findByUserAndActionCardType(user, insuranceName);
        if (damageCases != null && !damageCases.isEmpty()) {
            returnValue = damageCases.stream().mapToDouble(dc -> dc.getDamageAmountToPay()).sum();
        }
        return returnValue;
    }

    private double calculateSavedMoney(User user, String insuranceName) {
        double returnValue = 0.00;
        List<DamageCase> damageCases = damageCaseRepository.findByUserAndActionCardType(user, insuranceName);
        if (damageCases != null && !damageCases.isEmpty()) {
            returnValue = damageCases.stream().mapToDouble(dc -> dc.getDamageAmountRefund()).sum();
        }
        return returnValue;
    }
    
    private int getDamageCases(User user, String insuranceName) {
        int returnValue = 0;
        List<DamageCase> damageCases = damageCaseRepository.findByUserAndActionCardType(user, insuranceName);
        if (damageCases != null && !damageCases.isEmpty()) {
            returnValue = (int) damageCases.stream().count();
        }
        return returnValue;
    }

    private InsuranceSummary generateInsuranceSummary(UserGameStatus userGameStatus, User user, Insurance insurance) {

        InsuranceSummary returnValue = new InsuranceSummary();
        returnValue.setInsuranceName(insurance.getName());

        Optional<ContractedInsurance> optInsurance = contractedInsuranceRepository.findByUserAndInsuranceId(user, insurance.getId());
        if (optInsurance.isPresent()) {
            double yearlyCost = optInsurance.get().getYearlyCost();
            int yearsOfMembership = Level.getSIZE() - insurance.getLevel();
            double paidForInsuranceSum = yearsOfMembership * yearlyCost;
            double paidClaims = calculatePaidClaims(user, insurance.getName());
            double savedMoney = calculateSavedMoney(user, insurance.getName());
            
            returnValue.setDamageCases(getDamageCases(user, insurance.getName()));
            returnValue.setAgreed(true);
            returnValue.setYears(yearsOfMembership);
            returnValue.setPaidCosts(paidForInsuranceSum);
            returnValue.setPaidDamageCosts(paidClaims);
            returnValue.setSavedMoney(savedMoney);
            returnValue.setSelectedInsurance(messageSource.getMessage("decision.insurance.answer" + optInsurance.get().getSelectedChoice()
                    + ".id." + insurance.getId(), null, Locale.GERMANY));
        }

        return returnValue;
    }

}
