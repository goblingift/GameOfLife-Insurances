/*
 * Copyright (C) 2021 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.goli.controller;

import gift.goblin.goli.database.model.actioncards.CarInsuranceActionCard;
import gift.goblin.goli.dto.InsuranceSelection;
import gift.goblin.goli.enumerations.Insurance;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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

        model.addAttribute("display_success", true);
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

}
