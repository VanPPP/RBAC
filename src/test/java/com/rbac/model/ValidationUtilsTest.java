package com.rbac.model;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ValidationUtilsTest {
    @Test
    void testUsernameValidation() {
        assertTrue(ValidationUtils.isValidUsername("admin"));
        assertFalse(ValidationUtils.isValidUsername("a")); // слишком короткое
        assertFalse(ValidationUtils.isValidUsername("user!name")); // недопустимый символ
    }

    @Test
    void testEmailValidation() {
        assertTrue(ValidationUtils.isValidEmail("test@company.com"));
        assertFalse(ValidationUtils.isValidEmail("invalid-email"));
    }

    @Test
    void testDateValidation() {
        assertTrue(ValidationUtils.isValidDate("2026-12-31"));
        assertFalse(ValidationUtils.isValidDate("31-12-2026")); // неверный формат
    }
}