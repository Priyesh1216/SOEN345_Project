package com.example.soen345_project.domain;

/**
 * Centralized input validation utility.
 *
 * All methods return a non-null error message string on failure,
 * or null when the input is valid. This keeps Activities thin and
 * makes every rule independently testable.
 */
public class InputValidator {

    // E-mail: must match RFC 5322 simplified pattern
    private static final String EMAIL_REGEX =
            "^[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$";

    // Phone: E.164 format — leading '+', country code 1-3 digits, total 8-15 digits
    private static final String PHONE_REGEX = "^\\+[1-9]\\d{7,14}$";

    // Prevent instantiation
    private InputValidator() {}

    // -----------------------------------------------------------------------
    // Required-field checks
    // -----------------------------------------------------------------------

    /** Returns an error message if the value is null or blank, otherwise null. */
    public static String validateRequired(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            return fieldName + " is required";
        }
        return null;
    }

    // -----------------------------------------------------------------------
    // Format checks
    // -----------------------------------------------------------------------

    /**
     * Validates an e-mail address format.
     * The field is also checked for emptiness first.
     */
    public static String validateEmail(String email) {
        String requiredError = validateRequired(email, "Email");
        if (requiredError != null) return requiredError;
        if (!email.trim().matches(EMAIL_REGEX)) {
            return "Enter a valid email address (e.g. user@example.com)";
        }
        return null;
    }

    /**
     * Validates a phone number in E.164 format.
     * The field is also checked for emptiness first.
     */
    public static String validatePhone(String phone) {
        String requiredError = validateRequired(phone, "Phone number");
        if (requiredError != null) return requiredError;
        if (!phone.trim().matches(PHONE_REGEX)) {
            return "Use format: +15141234567";
        }
        return null;
    }

    /**
     * Validates a password — must be at least 6 characters.
     */
    public static String validatePassword(String password) {
        String requiredError = validateRequired(password, "Password");
        if (requiredError != null) return requiredError;
        if (password.trim().length() < 6) {
            return "Password must be at least 6 characters";
        }
        return null;
    }

    // -----------------------------------------------------------------------
    // Event-specific checks
    // -----------------------------------------------------------------------

    /**
     * Validates the total-seats string entered for an event.
     * Must be a parseable positive integer.
     *
     * @return null on success, an error message on failure.
     */
    public static String validateEventSeats(String seatsStr) {
        String requiredError = validateRequired(seatsStr, "Total seats");
        if (requiredError != null) return requiredError;
        try {
            int seats = Integer.parseInt(seatsStr.trim());
            if (seats <= 0) {
                return "Total seats must be greater than 0";
            }
        } catch (NumberFormatException e) {
            return "Total seats must be a valid number";
        }
        return null;
    }

    /**
     * Validates a ticket reservation quantity.
     * Must be a positive integer not exceeding the available seats.
     *
     * @param quantity     quantity the user wants to reserve
     * @param availableSeats seats still open on the event
     */
    public static String validateTicketQuantity(int quantity, int availableSeats) {
        if (quantity <= 0) {
            return "Quantity must be at least 1";
        }
        if (quantity > availableSeats) {
            return "Only " + availableSeats + " seat(s) available";
        }
        return null;
    }

    /**
     * Validates the date string for an event — must match yyyy-MM-dd.
     */
    public static String validateEventDate(String dateStr) {
        String requiredError = validateRequired(dateStr, "Date");
        if (requiredError != null) return requiredError;
        if (!dateStr.trim().matches("^\\d{4}-\\d{2}-\\d{2}$")) {
            return "Use date format: yyyy-MM-dd";
        }
        return null;
    }
}