package dev.dwidi.patientwebapp.utils;

import dev.dwidi.patientwebapp.exception.InvalidPostcodeException;

public class PostCodeValidator {
    public static void validatePostcode(String postcode, String state) {
        int postcodeInt;
        try {
            postcodeInt = Integer.parseInt(postcode);
        } catch (NumberFormatException e) {
            throw new InvalidPostcodeException("Invalid postcode format");
        }

        boolean isValid = switch (state) {
            case "NSW" -> postcodeInt >= 2000 && postcodeInt <= 2999;
            case "ACT" -> postcodeInt >= 2600 && postcodeInt <= 2618;
            case "VIC" -> postcodeInt >= 3000 && postcodeInt <= 3999;
            case "QLD" -> postcodeInt >= 4000 && postcodeInt <= 4999;
            case "SA" -> postcodeInt >= 5000 && postcodeInt <= 5799;
            case "WA" -> postcodeInt >= 6000 && postcodeInt <= 6797;
            case "TAS" -> postcodeInt >= 7000 && postcodeInt <= 7999;
            case "NT" -> postcodeInt >= 800 && postcodeInt <= 899;
            default -> false;
        };

        if (!isValid) {
            throw new InvalidPostcodeException(
                    String.format("Invalid postcode %s for state %s", postcode, state)
            );
        }
    }
}
