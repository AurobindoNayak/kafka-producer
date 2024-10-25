package com.ants.booklibrary.kafkaproducer.repo;

import com.ants.booklibrary.kafkaproducer.entity.LibraryEvent;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LibraryEventRepo extends MongoRepository<LibraryEvent, Integer> {
}
