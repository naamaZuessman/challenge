package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.CompensationDto;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger LOG = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public Employee create(Employee employee) {
        LOG.debug("Creating employee [{}]", employee);

        employee.setEmployeeId(UUID.randomUUID().toString());
        employeeRepository.insert(employee);

        return employee;
    }

    @Override
    public Employee read(String id) {
        LOG.debug("Reading employee with id [{}]", id);

        Employee employee = employeeRepository.findByEmployeeId(id);

        if (employee == null) {
            throw new RuntimeException("Invalid employeeId: " + id);
        }

        return employee;
    }

    @Override
    public Employee update(Employee employee) {
        LOG.debug("Updating employee [{}]", employee);

        return employeeRepository.save(employee);
    }

    //*****************************
    // ReportingStructure (Task #1)
    //*****************************
    /**
     * @param id - The employee id for which the reporting structure is requested
     * @return ReportingStructure containing:
     *  -   employee data as retrieved from the DB
     *  -   numberOfReports as computed ad hoc
     */
    @Override
    public ReportingStructure readStructure(String id) {
        LOG.debug("Creating reporting structure for employee with id [{}]", id);

        Employee employee = read(id);
        int numberOfReports = computeTotalDirectReports(id);
        return new ReportingStructure(employee, numberOfReports);
    }

    /**
     * @return the total number of direct reports and all of their distinct reports down the hierarchy tree, computed recursively.
     * Employee object is retrieved for each employee so that information about directReports is eagerly loaded and taken into account
     */
    private int computeTotalDirectReports(String employeeId) {
        Employee employee = read(employeeId);
        return Optional.ofNullable(employee.getDirectReports())
                .map(directReports ->
                        directReports.size() +
                        directReports.stream()
                                .map(Employee::getEmployeeId)
                                .mapToInt(this::computeTotalDirectReports)
                                .sum())
                .orElse(0);
    }

    //************************
    // Compensation (Task #2)
    //************************

    /**
     * @param id - the employee id for which the data is requested
     * @return compensation data (salary and lastUpdated fields from Employee document) along with the Employee data
     *          Return null fields for employee and compensation for invalid employee id
     *          Return null fields for compensation data when data doesn't exist
     */
    @Override
    public CompensationDto readCompensation(String id) { // Because salary is created, I assumed that no salary data is a plausible state, in which case the fields will have null values
        LOG.debug("Reading compensation with id [{}]", id);

        Employee employee = read(id);
        final Compensation compensation = employee.getCompensation();
        final Integer salary = Optional.ofNullable(compensation).map(Compensation::getSalary).orElse(null);
        final LocalDate effectiveDate = Optional.ofNullable(compensation).map(Compensation::getLastUpdated).orElse(null);
        CompensationDto compensationDto = new CompensationDto(employee, salary, effectiveDate);
        return compensationDto;
    }

    @Override
    /**
     * Create compensation data and store in Employee document.
     * Overriding existing data is not allowed (updates can be implemented as a separate endpoint). Returned fields would be null.
     */
    public CompensationDto createCompensation(CompensationDto compensationDto) {

        LOG.debug("Creating compensation [{}]", compensationDto);
        if (compensationDto == null
                || compensationDto.getEmployee() == null
                || Stream.of(compensationDto.getSalary(), compensationDto.getEffectiveDate())
                .anyMatch(Objects::isNull)) {
            throw new RuntimeException("Inside Creating compensation - invalid compensation input");
        }

        Employee employee = read(compensationDto.getEmployee().getEmployeeId());
        if (employee.getCompensation() != null) {
            throw new RuntimeException("Inside Creating compensation - compensation data already exists");
        }

        // Create and store compensation data in employee document
        Compensation comp = new Compensation(compensationDto.getSalary(), compensationDto.getEffectiveDate());
        employee.setCompensation(comp);
        update(employee);

        // Add employee data to compensationDto
        compensationDto.setEmployee(employee);
        return compensationDto;
    }

}
