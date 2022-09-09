package com.example.GringottsTool;

import com.example.GringottsTool.Exeptions.HealthExeption;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;

import java.util.List;

public final class HealthChecker {
    private final Logger log = LogManager.getLogger();
    private final List<Healthcheckable> healthcheckableList;
    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    public HealthChecker(final List<Healthcheckable> healthcheckableList) {
        this.healthcheckableList = healthcheckableList;
    }

    public void areAllAlive() {
        for (Healthcheckable healthcheckable : healthcheckableList) {
            try {
                healthcheckable.isAlive();
            } catch (HealthExeption e) {
                e.printStackTrace();
                SpringApplication.exit(applicationContext, () -> 2);
            }
        }
        log.info("Program is healthy");
    }
}
