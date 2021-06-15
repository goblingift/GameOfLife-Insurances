/*
 * Copyright (C) 2021 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.goli.database.repository;

import gift.goblin.goli.database.model.DamageCase;
import gift.goblin.goli.database.model.User;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 *
 * @author andre
 */
public interface DamageCaseRepository extends MongoRepository<DamageCase, String> {
    
    List<DamageCase> findByUserAndActionCardType(User user, String actionCardType);
    
}
