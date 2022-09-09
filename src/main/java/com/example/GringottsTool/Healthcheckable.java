package com.example.GringottsTool;

import com.example.GringottsTool.Exeptions.HealthExeption;

public interface Healthcheckable {
    void isAlive() throws HealthExeption;
}
