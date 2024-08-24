package org.onstage.stager.repository;

import org.onstage.stager.model.Stager;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StagerRepo extends MongoRepository<Stager, String> {
}