package com.example.GringottsTool;

import com.example.GringottsTool.Exeptions.HealthExeption;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public final class HealthChecker {
    private final Logger log = LogManager.getLogger();
    private final List<Healthcheckable> healthcheckableList;


    @Autowired
    public HealthChecker(final List<Healthcheckable> healthcheckableList) {
        this.healthcheckableList = healthcheckableList;
    }

    public void areAllAlive() throws HealthExeption {
        for (Healthcheckable healthcheckable : healthcheckableList) {
            healthcheckable.isAlive();
        }
        log.info("Program is healthy");
    }
}
