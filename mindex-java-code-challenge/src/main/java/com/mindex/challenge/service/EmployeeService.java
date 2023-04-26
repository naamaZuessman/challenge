package com.mindex.challenge.service;

import com.mindex.challenge.data.CompensationDto;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;

public interface EmployeeService {
    Employee create(Employee employee);
    Employee read(String id);
    Employee update(Employee employee);
    ReportingStructure readStructure(String id);
    CompensationDto readCompensation(String id);
    CompensationDto createCompensation(CompensationDto compensation);
}
