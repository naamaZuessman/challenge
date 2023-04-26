package com.mindex.challenge.serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.mindex.challenge.data.CompensationDto;
import com.mindex.challenge.data.Employee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

/**
 * @author Naama Tapiero
 * created on 4/25/23
 */
public class CompensationDeserializer extends JsonDeserializer<CompensationDto> {

    private static final Logger LOG = LoggerFactory.getLogger(CompensationDeserializer.class);

    @Override
    public CompensationDto deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {

        ObjectCodec oc = jsonParser.getCodec();
        JsonNode node = oc.readTree(jsonParser);
        if (node != null) {
            JsonNode employeeNode = node.get("employee");
            Employee employee = new Employee();
            if (employeeNode != null) { // We are not interested in reading any other of the employee properties
                employee.setEmployeeId(employeeNode.get("employeeId").asText());
                employee.setFirstName(employeeNode.get("firstName").asText());
                employee.setLastName(employeeNode.get("lastName").asText());
                employee.setDepartment(employeeNode.get("department").asText());
                employee.setPosition(employeeNode.get("position").asText());
            }
            Integer salary = getIntValue(node, "salary");
            LocalDate effectiveDate = extractDateFromNode(node, "effectiveDate");
            return new CompensationDto(employee, salary, effectiveDate);
        }
        return null;
    }

    public static LocalDate extractDateFromNode(JsonNode jsonNode, String fieldName){
        return Optional.ofNullable(jsonNode.get(fieldName))
                .map(JsonNode::asText)
                .filter(text -> !"null".equals(text))
                .map(LocalDate::parse)
                .orElse(null);
    }

    public static Integer getIntValue(JsonNode jsonNode, String fieldName) {
        final JsonNode fieldNode = jsonNode.get(fieldName);
        if (fieldNode == null || !fieldNode.canConvertToInt()) {
            return null;
        }
        return fieldNode.asInt();
    }

    /*
    ObjectCodec oc = jsonParser.getCodec();
        JsonNode node = oc.readTree(jsonParser);

        if (node != null) {
            long companyId = node.get("companyId").asLong();
            final Long employeeId = getLongValue(node, "employeeId");
            String site = Optional.ofNullable(node.get("site")).map(JsonNode::asText).orElse(null);

            AnchorType anchorType = Optional.ofNullable(node.get("anchorType"))
                    .map(anchorTypeNode -> AnchorType.valueOf(anchorTypeNode.asText().toUpperCase()))
                    .orElse(null);

            final String date = Optional.ofNullable(node.get("date")).map(JsonNode::asText).filter(text -> !"null".equals(text)).orElse(null);

            final DayOfWeek dayOfWeek = Optional.ofNullable(node.get("dayOfWeek"))
                    .map(JsonNode::asText)
                    .map(text -> DayOfWeek.valueOf(text))
                    .orElse(null);

            final JsonNode anchorTypeNode = node.get("anchorType");
            if (anchorTypeNode == null) {
                logger.error("anchorTypeNode was null: employeeId = " + employeeId +", date = " + date );
            }

            DayType anchorDayType = Optional.ofNullable(node.get("anchorDayType"))
                    .map(JsonNode::asText)
                    .map(anchorDayTypeStr -> DayType.valueOf(anchorDayTypeStr.toUpperCase()))
                    .orElse(null);

            AnchorDataObject anchorDataObject = new AnchorDataObject(companyId, site, employeeId, anchorType, date, dayOfWeek, anchorDayType);

            // memberIds
            List<Long> memberIds = SerializationUtils.getListOfLongsFromNode(node, "memberIds");
            if (memberIds != null && !memberIds.isEmpty()) {
                anchorDataObject.setMemberIds(memberIds);
            }

            // end date
            Optional.ofNullable(node.get("endDate"))
                    .ifPresent(endDate -> anchorDataObject.setEndDate(endDate.asText()));

            // frequency
            Optional.ofNullable(node.get("frequency")).ifPresent(frequency -> anchorDataObject.setFrequency(frequency.asText()));

            // managerId
            final Long managerId = getLongValue(node, "managerId");
            if (managerId != null) {
                anchorDataObject.setManagerId(managerId);
            }

            // teamId
            final Long teamId = getLongValue(node, "teamId");
            if (teamId != null) {
                anchorDataObject.setTeamId(teamId);
            }

            // managedTeamId
            final Long managedTeamId = getLongValue(node, "managedTeamId");
            if (managedTeamId != null) {
                anchorDataObject.setManagedTeamId(managedTeamId);
            }

            // Build DailyAnchorExceptionData
            JsonNode exceptionDataNode = node.get("exceptionData");
            if (exceptionDataNode != null) {
                String dayTypeStr = exceptionDataNode.get("requestedDayType").asText();
                DayType requestedDayType = DayType.valueOf(dayTypeStr.toUpperCase());

                String status = Optional.ofNullable(exceptionDataNode.get("status")).map(s -> s.asText()).orElse(null);

                List<String> reasons = SerializationUtils.getListOfStringsFromNode(exceptionDataNode, "reasons");

                // Add DailyAnchorExceptionData to anchorDataObject
                DailyAnchorExceptionData dailyAnchorExceptionData = new DailyAnchorExceptionData(requestedDayType, reasons, status);
                anchorDataObject.setExceptionData(dailyAnchorExceptionData);
            }

            return anchorDataObject;
        }
        logger.info("Null node sent to AnchorDataObjectDeserializer.... returning null");
        return null;
     */
}
