package com.upcode.lms.common.utils;

import org.apache.commons.lang3.RandomStringUtils;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class StringUtils {
    
    private static final Pattern NON_LATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");
    
    private StringUtils() {
        // Utility class
    }
    
    /**
     * Check if string is null or empty
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    /**
     * Check if string is not null and not empty
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }
    
    /**
     * Check if string is null, empty or contains only whitespace
     */
    public static boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    /**
     * Check if string is not blank
     */
    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }
    
    /**
     * Capitalize first letter of string
     */
    public static String capitalize(String str) {
        if (isEmpty(str)) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
    
    /**
     * Capitalize first letter of each word
     */
    public static String capitalizeWords(String str) {
        if (isEmpty(str)) {
            return str;
        }
        
        return Arrays.stream(str.split("\\s+"))
                .map(StringUtils::capitalize)
                .collect(Collectors.joining(" "));
    }
    
    /**
     * Convert string to slug format (URL-friendly)
     */
    public static String toSlug(String input) {
        if (isEmpty(input)) {
            return "";
        }
        
        String nowhitespace = WHITESPACE.matcher(input).replaceAll("-");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        String slug = NON_LATIN.matcher(normalized).replaceAll("");
        
        return slug.toLowerCase()
                .replaceAll("-{2,}", "-")
                .replaceAll("^-|-$", "");
    }
    
    /**
     * Generate random string of specified length
     */
    public static String generateRandomString(int length) {
        return RandomStringUtils.randomAlphanumeric(length);
    }
    
    /**
     * Generate random alphabetic string
     */
    public static String generateRandomAlphabetic(int length) {
        return RandomStringUtils.randomAlphabetic(length);
    }
    
    /**
     * Generate random numeric string
     */
    public static String generateRandomNumeric(int length) {
        return RandomStringUtils.randomNumeric(length);
    }
    
    /**
     * Mask email address for privacy
     */
    public static String maskEmail(String email) {
        if (isEmpty(email) || !email.contains("@")) {
            return email;
        }
        
        String[] parts = email.split("@");
        String username = parts[0];
        String domain = parts[1];
        
        if (username.length() <= 2) {
            return "*".repeat(username.length()) + "@" + domain;
        }
        
        return username.charAt(0) + "*".repeat(username.length() - 2) + 
               username.charAt(username.length() - 1) + "@" + domain;
    }
    
    /**
     * Mask phone number for privacy
     */
    public static String maskPhoneNumber(String phoneNumber) {
        if (isEmpty(phoneNumber)) {
            return phoneNumber;
        }
        
        String cleanNumber = phoneNumber.replaceAll("[^0-9]", "");
        
        if (cleanNumber.length() < 4) {
            return "*".repeat(cleanNumber.length());
        }
        
        return "*".repeat(cleanNumber.length() - 4) + cleanNumber.substring(cleanNumber.length() - 4);
    }
    
    /**
     * Truncate string to specified length with ellipsis
     */
    public static String truncate(String str, int maxLength) {
        if (isEmpty(str) || str.length() <= maxLength) {
            return str;
        }
        
        return str.substring(0, maxLength - 3) + "...";
    }
    
    /**
     * Remove all non-alphabetic characters
     */
    public static String removeNonAlphabetic(String str) {
        if (isEmpty(str)) {
            return str;
        }
        return str.replaceAll("[^a-zA-Z]", "");
    }
    
    /**
     * Remove all non-numeric characters
     */
    public static String removeNonNumeric(String str) {
        if (isEmpty(str)) {
            return str;
        }
        return str.replaceAll("[^0-9]", "");
    }
    
    /**
     * Remove all non-alphanumeric characters
     */
    public static String removeNonAlphanumeric(String str) {
        if (isEmpty(str)) {
            return str;
        }
        return str.replaceAll("[^a-zA-Z0-9]", "");
    }
    
    /**
     * Check if string contains only alphabetic characters
     */
    public static boolean isAlphabetic(String str) {
        if (isEmpty(str)) {
            return false;
        }
        return str.matches("[a-zA-Z]+");
    }
    
    /**
     * Check if string contains only numeric characters
     */
    public static boolean isNumeric(String str) {
        if (isEmpty(str)) {
            return false;
        }
        return str.matches("[0-9]+");
    }
    
    /**
     * Check if string contains only alphanumeric characters
     */
    public static boolean isAlphanumeric(String str) {
        if (isEmpty(str)) {
            return false;
        }
        return str.matches("[a-zA-Z0-9]+");
    }
    
    /**
     * Validate email format
     */
    public static boolean isValidEmail(String email) {
        if (isEmpty(email)) {
            return false;
        }
        
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
                           "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        
        return email.matches(emailRegex);
    }
    
    /**
     * Validate phone number format (basic validation)
     */
    public static boolean isValidPhoneNumber(String phoneNumber) {
        if (isEmpty(phoneNumber)) {
            return false;
        }
        
        String cleanNumber = phoneNumber.replaceAll("[^0-9]", "");
        return cleanNumber.length() >= 10 && cleanNumber.length() <= 15;
    }
    
    /**
     * Format phone number with dashes
     */
    public static String formatPhoneNumber(String phoneNumber) {
        if (isEmpty(phoneNumber)) {
            return phoneNumber;
        }
        
        String cleanNumber = removeNonNumeric(phoneNumber);
        
        if (cleanNumber.length() == 10) {
            return cleanNumber.replaceFirst("(\\d{3})(\\d{3})(\\d{4})", "$1-$2-$3");
        }
        
        return phoneNumber;
    }
    
    /**
     * Join list of strings with delimiter
     */
    public static String join(List<String> strings, String delimiter) {
        if (strings == null || strings.isEmpty()) {
            return "";
        }
        
        return strings.stream()
                .filter(StringUtils::isNotEmpty)
                .collect(Collectors.joining(delimiter));
    }
    
    /**
     * Split string and trim each part
     */
    public static List<String> splitAndTrim(String str, String delimiter) {
        if (isEmpty(str)) {
            return List.of();
        }
        
        return Arrays.stream(str.split(delimiter))
                .map(String::trim)
                .filter(StringUtils::isNotEmpty)
                .collect(Collectors.toList());
    }
    
    /**
     * Safe substring that doesn't throw IndexOutOfBoundsException
     */
    public static String safeSubstring(String str, int start, int end) {
        if (isEmpty(str)) {
            return str;
        }
        
        int actualStart = Math.max(0, Math.min(start, str.length()));
        int actualEnd = Math.max(actualStart, Math.min(end, str.length()));
        
        return str.substring(actualStart, actualEnd);
    }
    
    /**
     * Convert camelCase to snake_case
     */
    public static String camelToSnake(String camelCase) {
        if (isEmpty(camelCase)) {
            return camelCase;
        }
        
        return camelCase.replaceAll("([a-z])([A-Z]+)", "$1_$2").toLowerCase();
    }
    
    /**
     * Convert snake_case to camelCase
     */
    public static String snakeToCamel(String snakeCase) {
        if (isEmpty(snakeCase)) {
            return snakeCase;
        }
        
        String[] parts = snakeCase.split("_");
        StringBuilder camelCase = new StringBuilder(parts[0].toLowerCase());
        
        for (int i = 1; i < parts.length; i++) {
            camelCase.append(capitalize(parts[i]));
        }
        
        return camelCase.toString();
    }
}