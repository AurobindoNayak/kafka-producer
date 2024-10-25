package com.ants.booklibrary.kafkaproducer.producer;

import com.ants.booklibrary.kafkaproducer.entity.LibraryEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Component
public class LibraryEventProducer {
    private static final Logger logger = LoggerFactory.getLogger(LibraryEventProducer.class);
    @Autowired
    KafkaTemplate<Integer, String> kafkaTemplate;

    //Kafkatemplate is used to send message to the kafka topic
    //it takes tw arguments 1- key & the other is value
    //so in application.properties file we have declared key-serializer as Integer & value-serializer as Strng

    public void sendLibraryEvent(LibraryEvent libraryEvent) throws JsonProcessingException {
        Integer key = libraryEvent.getEventId();
        String value = new ObjectMapper().writeValueAsString(libraryEvent);

        ListenableFuture<SendResult<Integer, String>> sendResultListenableFuture = kafkaTemplate.sendDefault(key, value);

        sendResultListenableFuture.addCallback(new ListenableFutureCallback<SendResult<Integer, String>>() {
            @Override
            public void onFailure(Throwable ex) {
                handleFailure(key, value, ex);

            }

            @Override
            public void onSuccess(SendResult<Integer, String> result) {
                handleSuccess(key, value, result);
            }
        });
    }

    private void handleSuccess(Integer key, String value, SendResult<Integer, String> result) {
        logger.info("message successfully produced to the topic " + key + value + result.getRecordMetadata().partition());
    }

    private void handleFailure(Integer key, String value, Throwable ex) {
        logger.info("message sending failed for the key {} value {} and error {}" + key + value + ex.getMessage());
        try {
            throw ex;
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }


    public SendResult<Integer, String> sendSynchronousLibraryEvent(LibraryEvent libraryEvent) throws JsonProcessingException {
        Integer key = libraryEvent.getEventId();
        String value = new ObjectMapper().writeValueAsString(libraryEvent);
        SendResult<Integer, String> sendResult = null;
        try {
            sendResult = kafkaTemplate.sendDefault(key, value).get(1, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sendResult;

    }

    public void send(LibraryEvent libraryEvent) throws JsonProcessingException {
        Integer key = libraryEvent.getEventId();
        String value = new ObjectMapper().writeValueAsString(libraryEvent);
        String topicName = "library-topic";
        ProducerRecord<Integer, String> producerRecord = buildProducerRecord(topicName, key, value);
        //We can pass the topicName, key, value as a method signature as line 83
        //ListenableFuture<SendResult<Integer, String>> sendResultListenableFuture = kafkaTemplate.send(topicName, key, value);
       //At line 84 for the method parameters we are creating a method which will return ProducerRecord
        ListenableFuture<SendResult<Integer, String>> sendResultListenableFuture = kafkaTemplate.send(producerRecord);
        sendResultListenableFuture.addCallback(new ListenableFutureCallback<SendResult<Integer, String>>() {
            @Override
            public void onFailure(Throwable ex) {
                handleFailure(key, value, ex);

            }

            @Override
            public void onSuccess(SendResult<Integer, String> result) {
                handleSuccess(key, value, result);
            }
        });
    }

    private ProducerRecord<Integer, String> buildProducerRecord(String topicName, Integer key, String value) {
        return new ProducerRecord<>(topicName, null, key, value, null);

    }

}
