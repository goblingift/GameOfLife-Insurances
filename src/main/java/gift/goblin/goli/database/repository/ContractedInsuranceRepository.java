/*
 * Copyright (C) 2021 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.goli.database.repository;

import gift.goblin.goli.database.model.ContractedInsurance;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 *
 * @author andre
 */
public interface ContractedInsuranceRepository extends MongoRepository<ContractedInsurance, String> {
    
}
