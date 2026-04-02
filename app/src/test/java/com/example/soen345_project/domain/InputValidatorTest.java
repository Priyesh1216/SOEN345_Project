package com.example.soen345_project.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        InputValidatorTest.RequiredFieldTest.class,
        InputValidatorTest.EmailTest.class,
        InputValidatorTest.PhoneTest.class,
        InputValidatorTest.PasswordTest.class,
        InputValidatorTest.EventSeatsTest.class,
        InputValidatorTest.TicketQuantityTest.class,
        InputValidatorTest.EventDateTest.class
})
public class InputValidatorTest {

    // -----------------------------------------------------------------------
    // Required field
    // -----------------------------------------------------------------------
    public static class RequiredFieldTest {

        @Test
        public void validateRequired_nullValue_returnsError() {
            assertNotNull(InputValidator.validateRequired(null, "Name"));
        }

        @Test
        public void validateRequired_emptyString_returnsError() {
            assertNotNull(InputValidator.validateRequired("", "Name"));
        }

        @Test
        public void validateRequired_blankString_returnsError() {
            assertNotNull(InputValidator.validateRequired("   ", "Name"));
        }

        @Test
        public void validateRequired_validValue_returnsNull() {
            assertNull(InputValidator.validateRequired("John", "Name"));
        }

        @Test
        public void validateRequired_errorMessageContainsFieldName() {
            String error = InputValidator.validateRequired("", "Email");
            assertNotNull(error);
            assert error.contains("Email");
        }
    }

    // -----------------------------------------------------------------------
    // Email
    // -----------------------------------------------------------------------
    public static class EmailTest {

        @Test
        public void validateEmail_null_returnsError() {
            assertNotNull(InputValidator.validateEmail(null));
        }

        @Test
        public void validateEmail_empty_returnsError() {
            assertNotNull(InputValidator.validateEmail(""));
        }

        @Test
        public void validateEmail_missingAtSign_returnsError() {
            assertNotNull(InputValidator.validateEmail("userexample.com"));
        }

        @Test
        public void validateEmail_missingDomain_returnsError() {
            assertNotNull(InputValidator.validateEmail("user@"));
        }

        @Test
        public void validateEmail_missingTld_returnsError() {
            assertNotNull(InputValidator.validateEmail("user@example"));
        }

        @Test
        public void validateEmail_spaces_returnsError() {
            assertNotNull(InputValidator.validateEmail("user @example.com"));
        }

        @Test
        public void validateEmail_validSimple_returnsNull() {
            assertNull(InputValidator.validateEmail("user@example.com"));
        }

        @Test
        public void validateEmail_validWithSubdomain_returnsNull() {
            assertNull(InputValidator.validateEmail("user@mail.example.com"));
        }

        @Test
        public void validateEmail_validWithPlusAlias_returnsNull() {
            assertNull(InputValidator.validateEmail("user+tag@example.com"));
        }

        @Test
        public void validateEmail_validWithDots_returnsNull() {
            assertNull(InputValidator.validateEmail("first.last@example.org"));
        }
    }

    // -----------------------------------------------------------------------
    // Phone
    // -----------------------------------------------------------------------
    public static class PhoneTest {

        @Test
        public void validatePhone_null_returnsError() {
            assertNotNull(InputValidator.validatePhone(null));
        }

        @Test
        public void validatePhone_empty_returnsError() {
            assertNotNull(InputValidator.validatePhone(""));
        }

        @Test
        public void validatePhone_noLeadingPlus_returnsError() {
            assertNotNull(InputValidator.validatePhone("15141234567"));
        }

        @Test
        public void validatePhone_tooShort_returnsError() {
            // Less than 8 digits after country code
            assertNotNull(InputValidator.validatePhone("+1514123"));
        }

        @Test
        public void validatePhone_tooLong_returnsError() {
            // More than 15 digits total
            assertNotNull(InputValidator.validatePhone("+15141234567890123"));
        }

        @Test
        public void validatePhone_containsLetters_returnsError() {
            assertNotNull(InputValidator.validatePhone("+1514ABCDEFG"));
        }

        @Test
        public void validatePhone_validCanadian_returnsNull() {
            assertNull(InputValidator.validatePhone("+15141234567"));
        }

        @Test
        public void validatePhone_validUK_returnsNull() {
            assertNull(InputValidator.validatePhone("+447911123456"));
        }

        @Test
        public void validatePhone_validShortCountryCode_returnsNull() {
            assertNull(InputValidator.validatePhone("+12125551234"));
        }
    }

    // -----------------------------------------------------------------------
    // Password
    // -----------------------------------------------------------------------
    public static class PasswordTest {

        @Test
        public void validatePassword_null_returnsError() {
            assertNotNull(InputValidator.validatePassword(null));
        }

        @Test
        public void validatePassword_empty_returnsError() {
            assertNotNull(InputValidator.validatePassword(""));
        }

        @Test
        public void validatePassword_tooShort_returnsError() {
            assertNotNull(InputValidator.validatePassword("abc"));
        }

        @Test
        public void validatePassword_fiveChars_returnsError() {
            assertNotNull(InputValidator.validatePassword("abcde"));
        }

        @Test
        public void validatePassword_sixChars_returnsNull() {
            assertNull(InputValidator.validatePassword("abcdef"));
        }

        @Test
        public void validatePassword_longPassword_returnsNull() {
            assertNull(InputValidator.validatePassword("securePassword123!"));
        }
    }

    // -----------------------------------------------------------------------
    // Event seats
    // -----------------------------------------------------------------------
    public static class EventSeatsTest {

        @Test
        public void validateEventSeats_null_returnsError() {
            assertNotNull(InputValidator.validateEventSeats(null));
        }

        @Test
        public void validateEventSeats_empty_returnsError() {
            assertNotNull(InputValidator.validateEventSeats(""));
        }

        @Test
        public void validateEventSeats_zero_returnsError() {
            assertNotNull(InputValidator.validateEventSeats("0"));
        }

        @Test
        public void validateEventSeats_negative_returnsError() {
            assertNotNull(InputValidator.validateEventSeats("-5"));
        }

        @Test
        public void validateEventSeats_nonNumeric_returnsError() {
            assertNotNull(InputValidator.validateEventSeats("abc"));
        }

        @Test
        public void validateEventSeats_decimal_returnsError() {
            assertNotNull(InputValidator.validateEventSeats("10.5"));
        }

        @Test
        public void validateEventSeats_one_returnsNull() {
            assertNull(InputValidator.validateEventSeats("1"));
        }

        @Test
        public void validateEventSeats_hundredSeats_returnsNull() {
            assertNull(InputValidator.validateEventSeats("100"));
        }

        @Test
        public void validateEventSeats_withLeadingWhitespace_returnsNull() {
            assertNull(InputValidator.validateEventSeats("  50  "));
        }
    }

    // -----------------------------------------------------------------------
    // Ticket quantity
    // -----------------------------------------------------------------------
    public static class TicketQuantityTest {

        @Test
        public void validateTicketQuantity_zero_returnsError() {
            assertNotNull(InputValidator.validateTicketQuantity(0, 10));
        }

        @Test
        public void validateTicketQuantity_negative_returnsError() {
            assertNotNull(InputValidator.validateTicketQuantity(-1, 10));
        }

        @Test
        public void validateTicketQuantity_exceedsAvailable_returnsError() {
            assertNotNull(InputValidator.validateTicketQuantity(11, 10));
        }

        @Test
        public void validateTicketQuantity_exactlyAvailable_returnsNull() {
            assertNull(InputValidator.validateTicketQuantity(10, 10));
        }

        @Test
        public void validateTicketQuantity_one_returnsNull() {
            assertNull(InputValidator.validateTicketQuantity(1, 10));
        }

        @Test
        public void validateTicketQuantity_errorMessageContainsAvailableCount() {
            String error = InputValidator.validateTicketQuantity(5, 3);
            assertNotNull(error);
            assert error.contains("3");
        }

        @Test
        public void validateTicketQuantity_zeroAvailableSeats_returnsError() {
            assertNotNull(InputValidator.validateTicketQuantity(1, 0));
        }
    }

    // -----------------------------------------------------------------------
    // Event date
    // -----------------------------------------------------------------------
    public static class EventDateTest {

        @Test
        public void validateEventDate_null_returnsError() {
            assertNotNull(InputValidator.validateEventDate(null));
        }

        @Test
        public void validateEventDate_empty_returnsError() {
            assertNotNull(InputValidator.validateEventDate(""));
        }

        @Test
        public void validateEventDate_wrongFormat_ddMMyyyy_returnsError() {
            assertNotNull(InputValidator.validateEventDate("25-03-2026"));
        }

        @Test
        public void validateEventDate_wrongFormat_slash_returnsError() {
            assertNotNull(InputValidator.validateEventDate("2026/03/25"));
        }

        @Test
        public void validateEventDate_missingParts_returnsError() {
            assertNotNull(InputValidator.validateEventDate("2026-03"));
        }

        @Test
        public void validateEventDate_validDate_returnsNull() {
            assertNull(InputValidator.validateEventDate("2026-03-25"));
        }

        @Test
        public void validateEventDate_validDateFuture_returnsNull() {
            assertNull(InputValidator.validateEventDate("2030-12-31"));
        }
    }
}