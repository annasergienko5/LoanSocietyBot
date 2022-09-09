package com.example.GringottsTool;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class HealthCheckerResponse {
    @Autowired
    HealthChecker healthChecker;

    public ResponseEntity isAlive() {
        healthChecker.areAllAlive();
        return new ResponseEntity<>("Hellooooy", HttpStatus.OK);
    }
}
