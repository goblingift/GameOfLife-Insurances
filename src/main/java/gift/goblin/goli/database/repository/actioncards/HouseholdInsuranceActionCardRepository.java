/*
 * Copyright (C) 2021 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.goli.database.repository.actioncards;

import gift.goblin.goli.database.model.actioncards.HouseholdInsuranceActionCard;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 *
 * @author andre
 */
public interface HouseholdInsuranceActionCardRepository extends MongoRepository<HouseholdInsuranceActionCard, String> {
    
}
