package com.mindex.challenge.data;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mindex.challenge.serializer.CompensationDeserializer;
import com.mindex.challenge.serializer.CompensationSerializer;

import java.time.LocalDate;

/**
 * @author Naama Tapiero
 * created on 4/24/23
 */
@JsonSerialize(using = CompensationSerializer.class)
@JsonDeserialize(using = CompensationDeserializer.class)
public class CompensationDto {

    private Employee employee;
    private Integer salary;
    private LocalDate effectiveDate;

    public CompensationDto(Employee employee) {
        this.employee = employee;
    }

    public CompensationDto(Employee employee, Integer salary, LocalDate effectiveDate) {
        this.employee = employee;
        this.salary = salary;
        this.effectiveDate = effectiveDate;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Integer getSalary() {
        return salary;
    }

    public void setSalary(Integer salary) {
        this.salary = salary;
    }

    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(LocalDate effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    @Override
    public String toString() {
        return "CompensationDto{" +
                "employeeId=" + employee.getEmployeeId() +
                ", salary=" + salary +
                ", effectiveDate=" + effectiveDate +
                '}';
    }
}
