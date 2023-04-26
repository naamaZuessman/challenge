package com.mindex.challenge.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.mindex.challenge.data.CompensationDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * @author Naama Tapiero
 * created on 4/25/23
 */
public class CompensationSerializer extends JsonSerializer<CompensationDto> {

    private static final Logger LOG = LoggerFactory.getLogger(CompensationSerializer.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    @Override
    public void serialize(CompensationDto compensation, JsonGenerator jsonGenerator, SerializerProvider serializers) throws IOException {

        if (compensation == null) {
            LOG.error("Empty employee object... exiting");
            return;
        }
        jsonGenerator.writeStartObject();
        jsonGenerator.writeObjectField("employee", compensation.getEmployee());
        writeNumberFieldIfNotNull(jsonGenerator, "salary", compensation.getSalary());
        writeDateFieldIfNotNull(jsonGenerator, "effectiveDate", compensation.getEffectiveDate());

        jsonGenerator.writeEndObject();
    }

    public static void writeNumberFieldIfNotNull(JsonGenerator jsonGenerator, String name, Integer value) throws IOException {
        if (value == null) {
            jsonGenerator.writeNullField(name);
        } else {
            jsonGenerator.writeNumberField(name, value);
        }
    }

    public static void writeDateFieldIfNotNull(JsonGenerator jsonGenerator, String fieldName, LocalDate date) throws IOException{
        if (date == null) {
            jsonGenerator.writeNullField(fieldName);
        } else {
            String formattedDate = date.format(DATE_FORMATTER);
            try {
                jsonGenerator.writeStringField(fieldName, formattedDate);
            } catch (IOException e) {
                LOG.error("Exception thrown in writeDateFieldFromString(): date = [" + date + ", fieldName = [" + fieldName + "]");
            }
        }
    }

}