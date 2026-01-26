package com.financetracker.util;

public final class Constants {

    private Constants() {
        // Utility class
    }

    // Roles
    public static final String ROLE_USER = "USER";
    public static final String ROLE_ADMIN = "ADMIN";

    // JWT
    public static final String JWT_HEADER = "Authorization";
    public static final String JWT_PREFIX = "Bearer ";

    // Pagination defaults
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;

    // Default expense categories
    public static final String[] DEFAULT_EXPENSE_CATEGORIES = {
            "Food & Dining",
            "Transportation",
            "Shopping",
            "Entertainment",
            "Bills & Utilities",
            "Healthcare",
            "Education",
            "Travel",
            "Other"
    };
}
