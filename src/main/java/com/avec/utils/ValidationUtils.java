package com.avec.utils;

import java.math.BigDecimal;

public class ValidationUtils {

    public static boolean isValidBigDecimal(String value) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }
        try {
            new BigDecimal(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}