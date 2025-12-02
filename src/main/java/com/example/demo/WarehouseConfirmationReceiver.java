package com.example.demo;

import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Profile("producer")
public class WarehouseConfirmationReceiver {

    @KafkaListener(topics = "warehouse-confirmation", groupId = "warehouse-group")
    public void receiveConfirmation(String message) {
        System.out.println(">>> LAGERSTANDORT EMPFÄNGT: " + message);
    }
}