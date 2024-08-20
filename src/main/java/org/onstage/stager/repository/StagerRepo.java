package org.onstage.stager.repository;

import org.onstage.stager.model.StagerEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StagerRepo extends MongoRepository<StagerEntity, String> {
}