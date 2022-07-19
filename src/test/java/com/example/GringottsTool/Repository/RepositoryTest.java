package com.example.GringottsTool.Repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class RepositoryTest {

    @Test
    void isContains() {
        Assertions.assertTrue(Repository.isContains("Комиссаров Александр Любань", "Комиссаров Александр Любань"));
        Assertions.assertTrue(Repository.isContains("Комиссаров Александр Любань", "Комисс Любань"));
        Assertions.assertTrue(Repository.isContains("Комиссаров Александр Любань", "Любань"));
        Assertions.assertFalse(Repository.isContains("Комиссаров Александр Любань", "Михаил"));
    }

}