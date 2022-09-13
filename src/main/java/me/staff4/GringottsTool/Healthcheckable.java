package me.staff4.GringottsTool;

import me.staff4.GringottsTool.Exeptions.HealthExeption;

public interface Healthcheckable {
    void isAlive() throws HealthExeption;
}
