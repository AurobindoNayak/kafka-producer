package com.ants.booklibrary.kafkaproducer.controller;

import com.ants.booklibrary.kafkaproducer.entity.LibraryEvent;
import com.ants.booklibrary.kafkaproducer.entity.LibraryEventType;
import com.ants.booklibrary.kafkaproducer.producer.LibraryEventProducer;
import com.ants.booklibrary.kafkaproducer.repo.LibraryEventRepo;
import com.ants.booklibrary.kafkaproducer.service.SequenceGeneratorService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@RestController
public class ProducerController {

    @Autowired
    LibraryEventProducer libraryEventProducer;

    @Autowired
    private SequenceGeneratorService sequenceGeneratorService;

    @Autowired
    private LibraryEventRepo eventRepo;

    @PostMapping("/sendEvent")
    public ResponseEntity<LibraryEvent> sendEvent(@RequestBody LibraryEvent libraryEvent) throws JsonProcessingException, ExecutionException, InterruptedException {
        //using  sendDefault
        //libraryEventProducer.sendLibraryEvent(libraryEvent);
        //using  sendDefault
        //libraryEventProducer.sendSynchronousLibraryEvent(libraryEvent);
        //using only send

        if (libraryEvent.getEventId() == null) {
            libraryEvent.setEventId((int) sequenceGeneratorService.generateSequence("databaseSequence"));
        }
        libraryEventProducer.send(libraryEvent);
        libraryEvent.setLibraryEventType(LibraryEventType.NEW);
        LibraryEvent libraryEvents  = eventRepo.save(libraryEvent);
        return new ResponseEntity<>(libraryEvents, HttpStatus.OK);
    }

    @PutMapping("/updateEvent")
    public ResponseEntity<LibraryEvent> updateEvent(@RequestBody LibraryEvent libraryEvent) throws JsonProcessingException, ExecutionException, InterruptedException {
        if (libraryEvent.getEventId() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        libraryEvent.setLibraryEventType(LibraryEventType.UPDATED);
        libraryEvent.setBook(libraryEvent.getBook());
        libraryEvent.setEventId(libraryEvent.getEventId());
        libraryEventProducer.send(libraryEvent);
        return new ResponseEntity<>(eventRepo.save(libraryEvent), HttpStatus.CREATED);
    }
}
