package dev.dwidi.patientwebapp.utils;

import java.util.UUID;

public class RequestIdUtils {

    private RequestIdUtils() {
    }

    public static String generateRequestId() {
        return UUID.randomUUID().toString();
    }
}

