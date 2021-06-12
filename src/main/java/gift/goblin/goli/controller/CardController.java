/*
 * Copyright (C) 2021 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.goli.controller;

import gift.goblin.goli.database.model.actioncards.CarInsuranceActionCard;
import gift.goblin.goli.database.model.actioncards.DisabilityInsuranceActionCard;
import gift.goblin.goli.database.model.actioncards.HomeInsuranceActionCard;
import gift.goblin.goli.database.model.actioncards.HouseholdInsuranceActionCard;
import gift.goblin.goli.database.model.actioncards.LegalProtectionInsuranceActionCard;
import gift.goblin.goli.database.model.actioncards.LiabilityInsuranceActionCard;
import gift.goblin.goli.database.model.actioncards.SeniorAccidentInsuranceActionCard;
import gift.goblin.goli.database.model.actioncards.SmartphoneInsuranceActionCard;
import gift.goblin.goli.database.model.actioncards.TermLifeInsuranceActionCard;
import gift.goblin.goli.database.repository.actioncards.CarInsuranceActionCardRepository;
import gift.goblin.goli.database.repository.actioncards.DisabilityInsuranceActionCardRepository;
import gift.goblin.goli.database.repository.actioncards.HomeInsuranceActionCardRepository;
import gift.goblin.goli.database.repository.actioncards.HouseholdInsuranceActionCardRepository;
import gift.goblin.goli.database.repository.actioncards.LegalProtectionInsuranceActionCardRepository;
import gift.goblin.goli.database.repository.actioncards.LiabilityInsuranceActionCardRepository;
import gift.goblin.goli.database.repository.actioncards.SeniorAccidentInsuranceActionCardRepository;
import gift.goblin.goli.database.repository.actioncards.SmartphoneInsuranceActionCardRepository;
import gift.goblin.goli.database.repository.actioncards.TermLifeInsuranceActionCardRepository;
import gift.goblin.goli.dto.InsuranceSelection;
import gift.goblin.goli.enumerations.Insurance;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for the creation of new action cards.
 *
 * @author andre
 */
@Controller
@RequestMapping(CardController.BASE_URL_CARDS)
public class CardController {

    public static final String BASE_URL_CARDS = "/cards";

    private Logger logger = LoggerFactory.getLogger(this.getClass());

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

    @GetMapping(value = {"/create"})
    public String renderCreateCard(Model model) {
        logger.info("User opened add-actioncard.");

        model.addAttribute("insurances", getAvailableInsurances());
        return "add_actioncard";
    }

    private Map<Integer, String> getAvailableInsurances() {
        return Insurance.getValues().stream()
                .collect(Collectors.toMap((Insurance i) -> i.getId(), (Insurance i) -> i.getName()));
    }

    /**
     * Will get triggered if user selected an insurance in the dropdown.
     *
     * @param insuranceSelection
     * @param model
     * @return
     */
    @PostMapping(value = "/select-insurance")
    public String selectInsurance(InsuranceSelection insuranceSelection, Model model) {
        logger.info("Selected insurance: {}", insuranceSelection);

        Optional<String> optActionUrl = getActionUrl(insuranceSelection.getInsuranceId());
        if (optActionUrl.isPresent()) {
            model.addAttribute("actionUrl", optActionUrl.get());
        }

        model.addAttribute("selectedInsurance", insuranceSelection.getInsuranceId());
        return renderCreateCard(model);
    }

    /**
     * Will return the action-url for the given insurance, to add new
     * action-card for it.
     *
     * @param insuranceId id of the insurance
     * @return the action-url of the insurance, or empty Optional, if not found.
     */
    public Optional<String> getActionUrl(int insuranceId) {

        Optional<String> returnValue = Optional.empty();

        Optional<Insurance> optInsurance = Insurance.getValues().stream()
                .filter(i -> i.getId() == insuranceId).findFirst();
        if (optInsurance.isPresent()) {
            returnValue = Optional.of(optInsurance.get().getAddCardEndpoint());
        }
        return returnValue;
    }

    @PostMapping("/get-form")
    public String getFormContent(InsuranceSelection insuranceSelection, HttpSession session, Model model) {
        logger.info("Delivering fragment for insurance: {}", insuranceSelection.getInsuranceId());

        Optional<Insurance> optInsurance = Insurance.getValues().stream().filter(i -> i.getId() == insuranceSelection.getInsuranceId()).findAny();
        if (optInsurance.isPresent()) {

            Insurance selectedInsurance = optInsurance.get();
            model.addAttribute("actionUrl", selectedInsurance.getAddCardEndpoint());
            model.addAttribute("insuranceId", selectedInsurance.getId());
            return selectedInsurance.getTemplatePath();
        } else {
            logger.warn("User tried to get form for invalid insurance-id: {}", insuranceSelection);
            return null;
        }
    }

    @PostMapping(value = {"/add/smartphone-insurance"})
    public String createActionCardSmartphoneInsurance(SmartphoneInsuranceActionCard actionCard, BindingResult bindingResult, Model model) {

        logger.info("Create new actioncard for a disability-insurance: {}", actionCard);
        actionCard.setId(UUID.randomUUID().toString());
        SmartphoneInsuranceActionCard savedActionCard = smartphoneInsuranceActionCardRepository.save(actionCard);
        logger.info("Successful created new actionCard in database: {}", savedActionCard);

        model.addAttribute("display_success", true);
        return renderCreateCard(model);
    }

    @PostMapping(value = {"/add/home-insurance"})
    public String createActionCardHomeInsurance(HomeInsuranceActionCard actionCard, BindingResult bindingResult, Model model) {

        logger.info("Create new actioncard for a home-insurance: {}", actionCard);
        actionCard.setId(UUID.randomUUID().toString());
        HomeInsuranceActionCard savedActionCard = homeInsuranceActionCardRepository.save(actionCard);
        logger.info("Successful created new actionCard in database: {}", savedActionCard);

        model.addAttribute("display_success", true);
        return renderCreateCard(model);
    }

    @PostMapping(value = {"/add/termlife-insurance"})
    public String createActionCardTermLifeInsurance(TermLifeInsuranceActionCard actionCard, BindingResult bindingResult, Model model) {

        logger.info("Create new actioncard for a termlife-insurance: {}", actionCard);
        actionCard.setId(UUID.randomUUID().toString());
        TermLifeInsuranceActionCard savedActionCard = termLifeInsuranceActionCardRepository.save(actionCard);
        logger.info("Successful created new actionCard in database: {}", savedActionCard);

        model.addAttribute("display_success", true);
        return renderCreateCard(model);
    }

    @PostMapping(value = {"/add/senioraccident-insurance"})
    public String createActionCardSeniorAccidentInsurance(SeniorAccidentInsuranceActionCard actionCard, BindingResult bindingResult, Model model) {

        logger.info("Create new actioncard for a senioraccident-insurance: {}", actionCard);
        actionCard.setId(UUID.randomUUID().toString());
        SeniorAccidentInsuranceActionCard savedActionCard = seniorAccidentInsuranceActionCardRepository.save(actionCard);
        logger.info("Successful created new actionCard in database: {}", savedActionCard);

        model.addAttribute("display_success", true);
        return renderCreateCard(model);
    }

    @PostMapping(value = {"/add/legalprotection-insurance"})
    public String createActionCardLegalProtectionInsurance(LegalProtectionInsuranceActionCard actionCard, BindingResult bindingResult, Model model) {

        logger.info("Create new actioncard for a legalprotection-insurance: {}", actionCard);
        actionCard.setId(UUID.randomUUID().toString());
        LegalProtectionInsuranceActionCard savedActionCard = legalProtectionInsuranceActionCardRepository.save(actionCard);
        logger.info("Successful created new actionCard in database: {}", savedActionCard);

        model.addAttribute("display_success", true);
        return renderCreateCard(model);
    }

    /**
     * Action method which will get triggered if the user selected car-insurance
     * and submitted form.
     *
     * @param actionCard
     * @param bindingResult
     * @param model
     * @return
     */
    @PostMapping(value = {"/add/car-insurance"})
    public String createActionCardCarInsurance(CarInsuranceActionCard actionCard, BindingResult bindingResult, Model model) {

        logger.info("Create new actioncard for a car-insurance: {}", actionCard);
        actionCard.setId(UUID.randomUUID().toString());
        CarInsuranceActionCard savedActionCard = carInsuranceActionCardRepository.save(actionCard);
        logger.info("Successful created new actionCard in database: {}", savedActionCard);

        model.addAttribute("display_success", true);
        return renderCreateCard(model);
    }

    /**
     * Action method which will get triggered if the user selected
     * liability-insurance (Private Haftpflicht) and submitted the form.
     *
     * @param actionCard
     * @param bindingResult
     * @param model
     * @return
     */
    @PostMapping(value = {"/add/liability-insurance"})
    public String createActionCardLiabilityInsurance(LiabilityInsuranceActionCard actionCard, BindingResult bindingResult, Model model) {

        logger.info("Create new actioncard for a liability-insurance: {}", actionCard);
        actionCard.setId(UUID.randomUUID().toString());
        LiabilityInsuranceActionCard savedActionCard = liabilityInsuranceActionCardRepository.save(actionCard);
        logger.info("Successful created new actionCard in database: {}", savedActionCard);

        model.addAttribute("display_success", true);
        return renderCreateCard(model);
    }

    @PostMapping(value = {"/add/disability-insurance"})
    public String createActionCardDisabilityInsurance(DisabilityInsuranceActionCard actionCard, BindingResult bindingResult, Model model) {

        logger.info("Create new actioncard for a disability-insurance: {}", actionCard);
        actionCard.setId(UUID.randomUUID().toString());
        DisabilityInsuranceActionCard savedActionCard = disabilityInsuranceActionCardRepository.save(actionCard);
        logger.info("Successful created new actionCard in database: {}", savedActionCard);

        model.addAttribute("display_success", true);
        return renderCreateCard(model);
    }

    @PostMapping(value = {"/add/household-insurance"})
    public String createActionCardHouseholdInsurance(HouseholdInsuranceActionCard actionCard, BindingResult bindingResult, Model model) {

        logger.info("Create new actioncard for a household-insurance: {}", actionCard);
        actionCard.setId(UUID.randomUUID().toString());
        HouseholdInsuranceActionCard savedActionCard = householdInsuranceActionCardRepository.save(actionCard);
        logger.info("Successful created new actionCard in database: {}", savedActionCard);

        model.addAttribute("display_success", true);
        return renderCreateCard(model);
    }

}
