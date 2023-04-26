package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.CompensationDto;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EmployeeServiceImplTest {

    private String employeeUrl;
    private String employeeIdUrl;
    private String reportingStructureIdUrl;
    private String compensationUrl;
    private String compensationIdUrl;

    @Autowired
    private EmployeeService employeeService;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setup() {
        employeeUrl = "http://localhost:" + port + "/employee";
        employeeIdUrl = "http://localhost:" + port + "/employee/{id}";
        reportingStructureIdUrl = "http://localhost:" + port + "/reportingStructure/{id}";
        compensationUrl = "http://localhost:" + port + "/compensation";
        compensationIdUrl = "http://localhost:" + port + "/compensation/{id}";

    }

    @Test
    public void testCreateReadUpdate() {
        Employee testEmployee = new Employee();
        testEmployee.setFirstName("John");
        testEmployee.setLastName("Doe");
        testEmployee.setDepartment("Engineering");
        testEmployee.setPosition("Developer");

        // Create checks
        Employee createdEmployee = restTemplate.postForEntity(employeeUrl, testEmployee, Employee.class).getBody();
        final String createdEmployeeId = createdEmployee.getEmployeeId();
        assertNotNull(createdEmployeeId);
        assertEmployeeEquivalence(testEmployee, createdEmployee);


        // Read checks
        Employee readEmployee = restTemplate.getForEntity(employeeIdUrl, Employee.class, createdEmployeeId).getBody();
        assertEquals(createdEmployeeId, readEmployee.getEmployeeId());
        assertEmployeeEquivalence(createdEmployee, readEmployee);


        // Update checks
        readEmployee.setPosition("Development Manager");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Employee updatedEmployee =
                restTemplate.exchange(employeeIdUrl,
                        HttpMethod.PUT,
                        new HttpEntity<Employee>(readEmployee, headers),
                        Employee.class,
                        readEmployee.getEmployeeId()).getBody();

        assertEmployeeEquivalence(readEmployee, updatedEmployee);

        // Read after update check
        readEmployee = restTemplate.getForEntity(employeeIdUrl, Employee.class, createdEmployeeId).getBody();
        assertEquals(createdEmployeeId, readEmployee.getEmployeeId());
        assertEmployeeEquivalence(updatedEmployee, readEmployee);
    }

    private static void assertEmployeeEquivalence(Employee expected, Employee actual) {
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
        assertEquals(expected.getDepartment(), actual.getDepartment());
        assertEquals(expected.getPosition(), actual.getPosition());
    }

    //*****************************
    // ReportingStructure (Task #1)
    //*****************************
    @Test
    public void testReadStructure() {

        // 2-levels of direct report check
        ReportingStructure johnReportingStructure = restTemplate.getForEntity(
                reportingStructureIdUrl, ReportingStructure.class, "16a596ae-edd3-4847-99fe-c4518e82c86f").getBody();
        assertEquals(4, johnReportingStructure.getNumberOfReports().intValue());

        // Single-level of direct report check
        final ReportingStructure RingoReportingStructure = restTemplate.getForEntity(
            reportingStructureIdUrl, ReportingStructure.class, "03aa1462-ffa9-4978-901b-7c001562cf6f").getBody();
        assertEquals(2, RingoReportingStructure.getNumberOfReports().intValue());

        // No direct reports check
        final ReportingStructure PaulReportingStructure = restTemplate.getForEntity(
                reportingStructureIdUrl, ReportingStructure.class, "b7839309-3348-463b-a7e3-5de1c168beb3").getBody();
        assertEquals(0, PaulReportingStructure.getNumberOfReports().intValue());

        // Bad request - non-existing employee id check - return empty structure
        ReportingStructure emptyStructure = restTemplate.getForEntity(
                reportingStructureIdUrl, ReportingStructure.class, "no_id").getBody();
        assertEquals(null, emptyStructure.getEmployee());
        assertEquals(null, emptyStructure.getNumberOfReports());

        // Bad request - null employee id check - return empty structure
        emptyStructure = restTemplate.getForEntity(
                reportingStructureIdUrl, ReportingStructure.class, (Object) null).getBody();
        assertEquals(null, emptyStructure.getEmployee());
        assertEquals(null, emptyStructure.getNumberOfReports());
    }

    //************************
    // Compensation (Task #2)
    //************************
    @Test
    public void testReadAndCreateCompensation() {

        // Read stored compensation check
        final CompensationDto storedCompensationDto = restTemplate.getForEntity(
                compensationIdUrl, CompensationDto.class, "16a596ae-edd3-4847-99fe-c4518e82c86f").getBody();
        assertEquals("John", storedCompensationDto.getEmployee().getFirstName());
        assertEquals(50000, storedCompensationDto.getSalary().intValue());
        assertEquals(LocalDate.parse("1975-10-09"), storedCompensationDto.getEffectiveDate());

        // Read non-existing compensation check
        final String PaulId = "b7839309-3348-463b-a7e3-5de1c168beb3";
        final CompensationDto nullCompensationDto = restTemplate.getForEntity(
                compensationIdUrl, CompensationDto.class, PaulId).getBody();
        assertEquals(PaulId, nullCompensationDto.getEmployee().getEmployeeId());
        assertEquals("Paul", nullCompensationDto.getEmployee().getFirstName());
        assertEquals(null, nullCompensationDto.getSalary());
        assertEquals(null, nullCompensationDto.getEffectiveDate());

        // Create
        final Employee Paul = new Employee(PaulId);
        final LocalDate lastUpdated = LocalDate.parse("1980-01-01");

        // Bad request - missing Paul id in request - return empty object
        CompensationDto compensationDtoToCreate = new CompensationDto(new Employee(), 20000, lastUpdated);
        CompensationDto emptyObject = restTemplate.postForEntity(
                compensationUrl, compensationDtoToCreate, CompensationDto.class).getBody();
        assertEmptyCompensationFields(emptyObject);

        // Bad request - missing salary field check - return empty object
        CompensationDto missingSalaryCompensationDto = new CompensationDto(Paul, null, lastUpdated);
        emptyObject = restTemplate.postForEntity(
                compensationUrl, missingSalaryCompensationDto, CompensationDto.class).getBody();
        assertEmptyCompensationFields(emptyObject);

        // Bad request - missing lastUpdated field check - return empty object
        CompensationDto missingDateCompensationDto = new CompensationDto(Paul, 20000, null);
        emptyObject = restTemplate.postForEntity(
                compensationUrl, missingDateCompensationDto, CompensationDto.class).getBody();
        assertEmptyCompensationFields(emptyObject);

        // Create compensation data for Paul - success check
        compensationDtoToCreate = new CompensationDto(Paul, 20000, lastUpdated);
        CompensationDto created = restTemplate.postForEntity(
                compensationUrl, compensationDtoToCreate, CompensationDto.class).getBody();
        assertEquals("Paul", created.getEmployee().getFirstName());
        assertEquals(20000, created.getSalary().intValue());
        assertEquals(lastUpdated, created.getEffectiveDate());

        // Read after creation check
        CompensationDto storedAfterUpdateCompensationDto = restTemplate.getForEntity(
                compensationIdUrl, CompensationDto.class, Paul.getEmployeeId()).getBody();
        assertEquals("Paul", storedAfterUpdateCompensationDto.getEmployee().getFirstName());
        assertEquals(20000, storedAfterUpdateCompensationDto.getSalary().intValue());
        assertEquals(lastUpdated, storedAfterUpdateCompensationDto.getEffectiveDate());

        // Bad request - create compensation when already stored check - - return empty object
        Employee John = new Employee("16a596ae-edd3-4847-99fe-c4518e82c86f");
        LocalDate recentUpdated = LocalDate.parse("1982-01-01");
        CompensationDto duplicateCompensationDtoToCreate = new CompensationDto(John, 30000, recentUpdated);
        emptyObject = restTemplate.postForEntity(
                compensationUrl, duplicateCompensationDtoToCreate, CompensationDto.class).getBody();
        assertEmptyCompensationFields(emptyObject);
    }

    private static void assertEmptyCompensationFields(CompensationDto emptyObject) {
        assertEquals(null, emptyObject.getEmployee().getEmployeeId());
        assertEquals(null, emptyObject.getSalary());
        assertEquals(null, emptyObject.getEffectiveDate());
    }

}
