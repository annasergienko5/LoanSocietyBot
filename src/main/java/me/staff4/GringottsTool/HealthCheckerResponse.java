package me.staff4.GringottsTool;

import me.staff4.GringottsTool.Exeptions.HealthExeption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public final class HealthCheckerResponse {
    @Autowired
    private final ApplicationContext applicationContext;

    public HealthCheckerResponse(final ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

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
