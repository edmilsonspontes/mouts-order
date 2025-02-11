package com.mouts.esp.order.web.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

@RestController
@RequestMapping("/mongo-test")
public class MongoTestController {

    @Autowired
    private MongoTemplate mongoTemplate;

    @GetMapping("/check")
    public String checkMongoConnection() {
        try {
            return "Conexão com MongoDB funcionando: " + mongoTemplate.getDb().getName();
        } catch (Exception e) {
            return "ERRO: Não foi possível conectar ao MongoDB -> " + e.getMessage();
        }
    }
}
