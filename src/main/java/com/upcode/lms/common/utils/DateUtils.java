package com.upcode.lms.common.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class DateUtils {
    
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    public static final String DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DISPLAY_DATE_FORMAT = "dd/MM/yyyy";
    public static final String DISPLAY_DATETIME_FORMAT = "dd/MM/yyyy HH:mm:ss";
    
    private DateUtils() {
        // Utility class
    }
    
    /**
     * Get current date
     */
    public static LocalDate getCurrentDate() {
        return LocalDate.now();
    }
    
    /**
     * Get current date time
     */
    public static LocalDateTime getCurrentDateTime() {
        return LocalDateTime.now();
    }
    
    /**
     * Add days to current date
     */
    public static LocalDate addDays(int days) {
        return LocalDate.now().plusDays(days);
    }
    
    /**
     * Add days to given date
     */
    public static LocalDate addDays(LocalDate date, int days) {
        return date.plusDays(days);
    }
    
    /**
     * Calculate due date based on loan duration
     */
    public static LocalDate calculateDueDate(int loanDurationDays) {
        return LocalDate.now().plusDays(loanDurationDays);
    }
    
    /**
     * Calculate days between two dates
     */
    public static long daysBetween(LocalDate startDate, LocalDate endDate) {
        return ChronoUnit.DAYS.between(startDate, endDate);
    }
    
    /**
     * Check if a date is overdue
     */
    public static boolean isOverdue(LocalDate dueDate) {
        return LocalDate.now().isAfter(dueDate);
    }
    
    /**
     * Calculate overdue days
     */
    public static long calculateOverdueDays(LocalDate dueDate) {
        if (!isOverdue(dueDate)) {
            return 0;
        }
        return daysBetween(dueDate, LocalDate.now());
    }
    
    /**
     * Format date to string
     */
    public static String formatDate(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT));
    }
    
    /**
     * Format date to display string
     */
    public static String formatDateForDisplay(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern(DISPLAY_DATE_FORMAT));
    }
    
    /**
     * Format datetime to string
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern(DEFAULT_DATETIME_FORMAT));
    }
    
    /**
     * Format datetime to display string
     */
    public static String formatDateTimeForDisplay(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern(DISPLAY_DATETIME_FORMAT));
    }
    
    /**
     * Parse string to date
     */
    public static LocalDate parseDate(String dateString) {
        return LocalDate.parse(dateString, DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT));
    }
    
    /**
     * Parse string to datetime
     */
    public static LocalDateTime parseDateTime(String dateTimeString) {
        return LocalDateTime.parse(dateTimeString, DateTimeFormatter.ofPattern(DEFAULT_DATETIME_FORMAT));
    }
    
    /**
     * Check if date is today
     */
    public static boolean isToday(LocalDate date) {
        return date.equals(LocalDate.now());
    }
    
    /**
     * Check if date is in the past
     */
    public static boolean isPast(LocalDate date) {
        return date.isBefore(LocalDate.now());
    }
    
    /**
     * Check if date is in the future
     */
    public static boolean isFuture(LocalDate date) {
        return date.isAfter(LocalDate.now());
    }
    
    /**
     * Get start of day
     */
    public static LocalDateTime getStartOfDay(LocalDate date) {
        return date.atStartOfDay();
    }
    
    /**
     * Get end of day
     */
    public static LocalDateTime getEndOfDay(LocalDate date) {
        return date.atTime(23, 59, 59, 999999999);
    }
    
    /**
     * Check if two dates are the same day
     */
    public static boolean isSameDay(LocalDate date1, LocalDate date2) {
        return date1.equals(date2);
    }
    
    /**
     * Get the first day of current month
     */
    public static LocalDate getFirstDayOfCurrentMonth() {
        return LocalDate.now().withDayOfMonth(1);
    }
    
    /**
     * Get the last day of current month
     */
    public static LocalDate getLastDayOfCurrentMonth() {
        LocalDate now = LocalDate.now();
        return now.withDayOfMonth(now.lengthOfMonth());
    }
    
    /**
     * Calculate age from birth date
     */
    public static int calculateAge(LocalDate birthDate) {
        return (int) ChronoUnit.YEARS.between(birthDate, LocalDate.now());
    }
    
    /**
     * Check if a date is within a range
     */
    public static boolean isDateInRange(LocalDate date, LocalDate startDate, LocalDate endDate) {
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }
}