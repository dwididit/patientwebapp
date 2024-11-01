package dev.dwidi.patientwebapp.utils;

import dev.dwidi.patientwebapp.constant.ApplicationConstant;
import dev.dwidi.patientwebapp.exception.DateInvalidFormatException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateValidator {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;

    private DateValidator() {

    }

    public static void validate(String date) {
        if (date == null) {
            throw new DateInvalidFormatException(
                    String.format("Date cannot be null. Expected format: %s", ApplicationConstant.DATE_PATTERN)
            );
        }

        try {
            LocalDate.parse(date, formatter);
        } catch (DateTimeParseException e) {
            throw new DateInvalidFormatException(
                    String.format("Invalid date format. Expected format: %s", ApplicationConstant.DATE_PATTERN)
            );
        }
    }


}
