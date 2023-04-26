package com.mindex.challenge.data;

import java.time.LocalDate;

/**
 * @author Naama Tapiero
 * created on 4/24/23
 */
public class Compensation {

    private int salary;
    private LocalDate lastUpdated;

    public Compensation() {
    }

    public Compensation(int salary, LocalDate lastUpdated) {
        this.salary = salary;
        this.lastUpdated = lastUpdated;
    }

    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

    public LocalDate getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDate lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
