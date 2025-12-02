package com.example.demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/warehouse")
@Profile("producer")
public class WarehouseSender {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @PostMapping("/send")
    public String sendWarehouseData(@RequestBody WarehouseData data) throws JsonProcessingException {
        data.setTimestamp(Instant.now().toString());

        String json = new ObjectMapper().writeValueAsString(data);

        kafkaTemplate.send("warehouse-input", json);

        return "GESENDET: " + json;
    }
}