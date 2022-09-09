package com.example.GringottsTool;

import com.example.GringottsTool.Exeptions.HealthExeption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public final class HealthCheckerResponse {
    @Autowired
    private final ApplicationContext applicationContext = new AnnotationConfigApplicationContext(Config.class);

    public ResponseEntity isAlive() {
        HealthChecker healthChecker = (HealthChecker) applicationContext.getBean("healthChecker");
        try {
            healthChecker.areAllAlive();
        } catch (HealthExeption e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
        }
        return new ResponseEntity<>("everything works", HttpStatus.OK);
    }
}
