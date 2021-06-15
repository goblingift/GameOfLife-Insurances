/*
 * Copyright (C) 2021 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.goli.service;

import gift.goblin.goli.database.model.ContractedInsurance;
import gift.goblin.goli.database.model.User;
import gift.goblin.goli.database.repository.ContractedInsuranceRepository;
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
import gift.goblin.goli.dto.InsuranceSummary;
import gift.goblin.goli.enumerations.Insurance;
import gift.goblin.goli.enumerations.Level;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Contains several methods to convert the different insurances for summary statistics.
 * @author andre
 */
public class InsuranceSummaryConverter {
    
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
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
    ContractedInsuranceRepository contractedInsuranceRepository;
    
    @Autowired
    UserRepository userRepository;
    
    
    public InsuranceSummary summarizeSmartphoneInsurance(String username) {
        
        
        return null;
    }
    
    public InsuranceSummary summarizeCarInsurance(String username) {
        
        InsuranceSummary insuranceSummary = new InsuranceSummary(Insurance.CAR_INSURANCE.getName());
        
        User user = userRepository.findByFullname(username);
        Optional<ContractedInsurance> optContractedInsurance = contractedInsuranceRepository.findByUserAndInsuranceId(user, Insurance.CAR_INSURANCE.getId());
        if (optContractedInsurance.isEmpty()) {
            insuranceSummary.setAgreed(false);
        } else {
            insuranceSummary.setAgreed(true);
            int paidYears = Level.getSIZE() - Insurance.CAR_INSURANCE.getLevel();
            ContractedInsurance contractedInsurance = optContractedInsurance.get();
            double paidFeeSum = contractedInsurance.getYearlyCost() * paidYears;
            
            insuranceSummary.setYears(paidYears);
            insuranceSummary.setPaidCosts(paidFeeSum);
            
        }
        
        return insuranceSummary;
    }
    
    
    
}
