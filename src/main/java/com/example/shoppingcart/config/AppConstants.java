package com.example.shoppingcart.config;

/**
 * Application constants to replace hardcoded values throughout the application.
 * All constants are organized by functional area for better maintainability.
 */
public final class AppConstants {

    private AppConstants() {
        // Prevent instantiation
    }

    // ============================================
    // CART CONFIGURATION CONSTANTS
    // ============================================
    public static final class Cart {
        public static final int DEFAULT_MAX_ITEMS = 100;
        public static final int DEFAULT_SESSION_TIMEOUT_MINUTES = 60;
        public static final String CART_ID_PREFIX = "CART-";
        public static final String DIRECT_CHECKOUT_PREFIX = "DIRECT_";

        private Cart() {}
    }

    // ============================================
    // PRICING CONFIGURATION CONSTANTS
    // ============================================
    public static final class Pricing {
        public static final String DEFAULT_REGION = "UK";
        public static final String DEFAULT_CURRENCY = "GBP";
        public static final int DEFAULT_DECIMAL_PLACES = 2;
        public static final String PRICE_SCALE_MODE = "HALF_UP";
        public static final int PENCE_TO_POUNDS_DIVISOR = 100;

        private Pricing() {}
    }

    // ============================================
    // PRODUCT CONFIGURATION CONSTANTS
    // ============================================
    public static final class Product {
        public static final int MAX_PRODUCT_ID_LENGTH = 8;
        public static final int MAX_PRODUCT_NAME_LENGTH = 100;
        public static final int MAX_DESCRIPTION_LENGTH = 500;
        public static final String PRODUCT_ID_PATTERN = "^[A-Za-z0-9]+$";
        public static final int DEFAULT_STOCK_LEVEL = 0;
        public static final int MIN_REORDER_POINT = 5;

        private Product() {}
    }

    // ============================================
    // DISCOUNT CONFIGURATION CONSTANTS
    // ============================================
    public static final class Discount {
        public static final int MAX_APPLICABLE_DISCOUNTS = 5;
        public static final int DEFAULT_PRIORITY = 1;
        public static final int CACHE_DURATION_MINUTES = 30;
        public static final String PERCENTAGE_TYPE = "PERCENTAGE";
        public static final String FIXED_AMOUNT_TYPE = "FIXED_AMOUNT";

        private Discount() {}
    }

    // ============================================
    // VALIDATION CONSTANTS
    // ============================================
    public static final class Validation {
        public static final int MIN_QUANTITY = 1;
        public static final int MAX_QUANTITY_PER_ITEM = 100;
        public static final int MIN_PASSWORD_LENGTH = 8;
        public static final int MAX_LOGIN_ATTEMPTS = 5;
        public static final int LOCKOUT_DURATION_MINUTES = 5;

        private Validation() {}
    }

    // ============================================
    // CACHE CONFIGURATION CONSTANTS
    // ============================================
    public static final class CacheConstants {
        public static final String PRODUCTS_CACHE = "products";
        public static final String CART_CACHE = "carts";
        public static final String CART_TOTALS_CACHE = "cartTotals";
        public static final String DISCOUNT_RULES_CACHE = "discountRules";
        public static final String PRODUCT_PRICES_CACHE = "productPrices";
        public static final String USER_SESSIONS_CACHE = "userSessions";

        public static final int DEFAULT_TTL_SECONDS = 3600; // 1 hour
        public static final int PRODUCT_TTL_SECONDS = 7200; // 2 hours
        public static final int CART_TTL_SECONDS = 1800; // 30 minutes
        public static final int DISCOUNT_TTL_SECONDS = 1800; // 30 minutes

        private CacheConstants() {}
    }

    // ============================================
    // KAFKA TOPIC CONSTANTS
    // ============================================
    public static final class Topics {
        public static final String CART_EVENTS = "cart-events";
        public static final String CHECKOUT_EVENTS = "checkout-events";
        public static final String INVENTORY_EVENTS = "inventory-events";
        public static final String PRICING_EVENTS = "pricing-events";
        public static final String USER_EVENTS = "user-events";
        public static final String AUDIT_EVENTS = "audit-events";

        private Topics() {}
    }

    // ============================================
    // API CONFIGURATION CONSTANTS
    // ============================================
    public static final class Api {
        public static final String BASE_PATH = "/api";
        public static final String VERSION_HEADER = "X-API-Version";
        public static final String CURRENT_VERSION = "v1";
        public static final String CONTENT_TYPE_JSON = "application/json";
        public static final String CHARSET_UTF8 = "UTF-8";

        public static final int DEFAULT_PAGE_SIZE = 20;
        public static final int MAX_PAGE_SIZE = 100;
        public static final String DEFAULT_SORT_FIELD = "createdAt";
        public static final String DEFAULT_SORT_DIRECTION = "DESC";

        private Api() {}
    }

    // ============================================
    // SECURITY CONSTANTS
    // ============================================
    public static final class Security {
        public static final String JWT_HEADER = "Authorization";
        public static final String JWT_PREFIX = "Bearer ";
        public static final String ROLE_USER = "USER";
        public static final String ROLE_ADMIN = "ADMIN";
        public static final String ROLE_MANAGER = "MANAGER";

        public static final int JWT_EXPIRATION_HOURS = 24;
        public static final int REFRESH_TOKEN_EXPIRATION_DAYS = 7;
        public static final int SESSION_TIMEOUT_MINUTES = 30;

        private Security() {}
    }

    // ============================================
    // DATABASE CONSTANTS
    // ============================================
    public static final class Database {
        public static final String SCHEMA_VERSION = "1.0";
        public static final int BATCH_SIZE = 20;
        public static final int CONNECTION_POOL_SIZE = 10;
        public static final int QUERY_TIMEOUT_SECONDS = 30;

        // Table names
        public static final String PRODUCTS_TABLE = "products";
        public static final String CARTS_TABLE = "shopping_carts";
        public static final String CART_ITEMS_TABLE = "cart_items";
        public static final String USERS_TABLE = "users";
        public static final String DISCOUNT_RULES_TABLE = "discount_rules";

        private Database() {}
    }

    // ============================================
    // ERROR MESSAGE CONSTANTS
    // ============================================
    public static final class MessageConstants {
        public static final String CART_NOT_FOUND = "Shopping cart not found";
        public static final String PRODUCT_NOT_FOUND = "Product not found";
        public static final String INVALID_QUANTITY = "Invalid quantity specified";
        public static final String INSUFFICIENT_STOCK = "Insufficient stock available";
        public static final String CHECKOUT_FAILED = "Checkout process failed";
        public static final String UNAUTHORIZED_ACCESS = "Unauthorized access";
        public static final String VALIDATION_FAILED = "Input validation failed";

        private MessageConstants() {}
    }

    // ============================================
    // REGEX PATTERNS
    // ============================================
    public static final class Patterns {
        public static final String PRODUCT_ID = "^[A-Za-z0-9]{1,8}$";
        public static final String EMAIL = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        public static final String PHONE = "^\\+?[1-9]\\d{1,14}$";
        public static final String CURRENCY_CODE = "^[A-Z]{3}$";
        public static final String REGION_CODE = "^[A-Z]{2,10}$";
        public static final String CART_ID = "^CART-[A-Z0-9]{8}$";

        private Patterns() {}
    }

    // ============================================
    // CURRENCY AND REGION CONSTANTS
    // ============================================
    public static final class Currency {
        public static final String GBP = "GBP";
        public static final String USD = "USD";
        public static final String EUR = "EUR";
        public static final String CAD = "CAD";
        public static final String AUD = "AUD";

        private Currency() {}
    }

    public static final class RegionConstants {
        public static final String UK = "UK";
        public static final String US = "US";
        public static final String EU = "EU";
        public static final String CA = "CA";
        public static final String AU = "AU";

        private RegionConstants() {}
    }
}