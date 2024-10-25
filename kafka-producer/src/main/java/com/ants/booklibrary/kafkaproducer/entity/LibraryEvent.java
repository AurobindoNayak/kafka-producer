package com.ants.booklibrary.kafkaproducer.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "libraryEvent")
public class LibraryEvent {
    private Integer eventId;
    private LibraryEventType libraryEventType;
    @DBRef
    private Book book;
}
