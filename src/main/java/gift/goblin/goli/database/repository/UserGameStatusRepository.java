/*
 * Copyright (C) 2021 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.goli.database.repository;

import gift.goblin.goli.database.model.UserGameStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 *
 * @author andre
 */
public interface UserGameStatusRepository extends MongoRepository<UserGameStatus, String>{
    
    UserGameStatus findByUsername(String username);
}
