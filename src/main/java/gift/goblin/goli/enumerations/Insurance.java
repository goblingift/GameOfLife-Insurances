/*
 * Copyright (C) 2021 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.goli.enumerations;

import gift.goblin.goli.controller.CardController;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author andre
 */
public enum Insurance {
    
    SMARTPHONE_INSURANCE(100, 1, "Smartphone-Versicherung", 0.00, 69.00, 96.00, CardController.BASE_URL_CARDS + "/add/smartphone-insurance", "/actioncards/add_smartphone_insurance :: replace_fragment"),
    CAR_INSURANCE(101, 3, "KFZ-Versicherung", 600.00, 900.00, 1200.00, CardController.BASE_URL_CARDS + "/add/car-insurance", "/actioncards/add_car_insurance :: replace_fragment"),
    LIABILITY_INSURANCE(102, 4, "Haftpflichtversicherung", 0.00, 35.00, 70.00, CardController.BASE_URL_CARDS + "/add/liability-insurance", "/actioncards/add_liability_insurance :: replace_fragment"),
    DISABILITY_INSURANCE(103, 5, "Berufsunfähigkeitsversicherung", 0.00, 600.00, 1200.00, CardController.BASE_URL_CARDS + "/add/disability-insurance", "/actioncards/add_disability_insurance :: replace_fragment"),
    HOUSEHOLD_INSURANCE(104, 8, "Hausratsversicherung", 0.00, 500.00, 800.00, CardController.BASE_URL_CARDS + "/add/household-insurance", "/actioncards/add_household_insurance :: replace_fragment"),
    HOME_INSURANCE(105, 18, "Wohngebäudeversicherung", 0.00, 700.00, 850.00, CardController.BASE_URL_CARDS + "/add/home-insurance", "/actioncards/add_home_insurance :: replace_fragment"),
    TERMLIFE_INSURANCE(106, 22, "Risikolebensversicherung", 0.00, 150.00, 300.00, CardController.BASE_URL_CARDS + "/add/termlife-insurance", "/actioncards/add_termlife_insurance :: replace_fragment"),
    SENIORACCIDENT_INSURANCE(107, 30, "Seniorenunfallversicherung", 0.00, 110.00, 200.00, CardController.BASE_URL_CARDS + "/add/senioraccident-insurance", "/actioncards/add_senioraccident_insurance :: replace_fragment"),
    LEGALPROTECTION_INSURANCE(108, 14, "Rechtsschutzversicherung", 180.00, 360.00, 540.00, CardController.BASE_URL_CARDS + "/add/legalprotection-insurance", "/actioncards/add_legalprotection_insurance :: replace_fragment");
    
    private Insurance(int id, int level, String name, double priceYearly1, double priceYearly2, double priceYearly3, String addCardEndpoint, String templatePath) {
        this.id = id;
        this.level = level;
        this.name = name;
        this.priceYearly1 = priceYearly1;
        this.priceYearly2 = priceYearly2;
        this.priceYearly3 = priceYearly3;
        this.addCardEndpoint = addCardEndpoint;
        this.templatePath = templatePath;
    }
    
    private static final List<Insurance> VALUES = Collections.unmodifiableList(Arrays.asList(values()));
    private static final int SIZE = VALUES.size();
    
    private int id;
    private int level;
    private String name;
    private double priceYearly1;
    private double priceYearly2;
    private double priceYearly3;
    // Contains the URL, which can be called to create new actioncard for this insurance-type
    private String addCardEndpoint;
    private String templatePath;

    public static List<Insurance> getValues() {
        return VALUES;
    }
    
    public static Optional<Insurance> findById(int id) {
        return VALUES.stream()
                .filter(p -> p.getId() == id)
                .findFirst();
    }
    
    public static Optional<Insurance> findByLevel(int level) {
        return VALUES.stream()
                .filter(p -> p.getLevel()== level)
                .findFirst();
    }
    
    public static Optional<Insurance> findByName(String name) {
        return VALUES.stream()
                .filter(p -> p.getName().equalsIgnoreCase(name))
                .findFirst();
    }
    
    public int getId() {
        return id;
    }

    public int getLevel() {
        return level;
    }

    public String getName() {
        return name;
    }

    public double getPriceYearly1() {
        return priceYearly1;
    }

    public double getPriceYearly2() {
        return priceYearly2;
    }

    public double getPriceYearly3() {
        return priceYearly3;
    }

    public String getAddCardEndpoint() {
        return addCardEndpoint;
    }

    public String getTemplatePath() {
        return templatePath;
    }
    
}
