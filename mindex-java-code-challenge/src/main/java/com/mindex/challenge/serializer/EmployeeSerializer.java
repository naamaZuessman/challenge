package com.mindex.challenge.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.mindex.challenge.data.Employee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Naama Tapiero
 * created on 4/25/23
 */
public class EmployeeSerializer extends JsonSerializer<Employee> {

    private static final Logger LOG = LoggerFactory.getLogger(EmployeeSerializer.class);

    @Override
    public void serialize(Employee employee, JsonGenerator jsonGenerator, SerializerProvider serializers) throws IOException {

        if (employee == null) {
            LOG.error("Empty employee object... exiting");
            return;
        }
        jsonGenerator.writeStartObject();
        serializeEmployeeObject(employee, jsonGenerator);
        jsonGenerator.writeEndObject();
    }

    public static void serializeEmployeeObject(Employee employee, JsonGenerator jsonGenerator) throws IOException {
        writeStringFieldIfNotNull(jsonGenerator, "employeeId", employee.getEmployeeId());
        writeStringFieldIfNotNull(jsonGenerator, "firstName", employee.getFirstName());
        writeStringFieldIfNotNull(jsonGenerator, "lastName", employee.getLastName());
        writeStringFieldIfNotNull(jsonGenerator, "position", employee.getPosition());
        writeStringFieldIfNotNull(jsonGenerator, "department", employee.getDepartment());

        final List<Employee> directReports = Optional.ofNullable(employee.getDirectReports()).orElseGet(ArrayList::new);
        final List<String> directReportIds = directReports.stream().map(Employee::getEmployeeId).collect(Collectors.toList());
        writeListOfObjectsAsArray(jsonGenerator, "directReports", directReportIds);
    }

    public static void writeStringFieldIfNotNull(JsonGenerator jsonGenerator, String name, String value) throws IOException {
        if (value == null) {
            jsonGenerator.writeNullField(name);
        } else {
            jsonGenerator.writeStringField(name, value);
        }
    }

    public static void writeListOfObjectsAsArray(JsonGenerator jsonGenerator, String fieldName, List<?> listOfObjects) throws IOException {
        if (!listOfObjects.isEmpty()) {
            jsonGenerator.writeFieldName(fieldName);
            jsonGenerator.writeStartArray();
            for (Object obj : listOfObjects){
                jsonGenerator.writeObject(obj);
            }
            jsonGenerator.writeEndArray();
        }
    }
}
