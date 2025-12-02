package com.example.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

@Component
@Profile("consumer")
public class CentralConsumer {

    @Autowired
    private CentralDataAggregator aggregator;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private static final String LOG_FILE = "warehouse_log.txt";

    @KafkaListener(topics = "warehouse-input", groupId = "central-group")
    public void receiveMessage(String message) {
        try {
            System.out.println("ZENTRALE: Nachricht empfangen: " + message);

            // 1. JSON in Objekt wandeln
            WarehouseData data = new ObjectMapper().readValue(message, WarehouseData.class);

            // 2. Daten speichern
            aggregator.add(data);

            // 3. In Datei loggen
            logToFile(message);

            // 4. Bestätigung zurücksenden
            String confirmation = "SUCCESS for Warehouse ID: " + data.getWarehouseId();
            kafkaTemplate.send("warehouse-confirmation", confirmation);

        } catch (Exception e) {
            System.err.println("Fehler: " + e.getMessage());
        }
    }

    private void logToFile(String data) {
        try {
            File file = new File(LOG_FILE);
            // Append Mode (true)
            try (FileWriter fw = new FileWriter(file, true);
                 BufferedWriter bw = new BufferedWriter(fw);
                 PrintWriter out = new PrintWriter(bw)) {
                out.println(data);
            }
        } catch (Exception e) {
            System.err.println("Konnte nicht loggen: " + e.getMessage());
        }
    }
}