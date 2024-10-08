package org.onstage.rehearsal.repository;

import org.onstage.rehearsal.model.Rehearsal;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RehearsalRepo extends MongoRepository<Rehearsal, String> {
}
