package com.hotelworks.enums;

/**
 * Module names for permission management
 */
public enum ModuleName {
    RESERVATIONS("reservations"),
    CHECKINS("checkins"),
    CHECKOUTS("checkouts"),
    ROOMS("rooms"),
    ROOM_TYPES("room-types"),
    PAYMENTS("payments"),
    ADVANCES("advances"),
    BILLS("bills"),
    TRANSACTIONS("transactions"),
    CHARGES("charges"),
    REPORTS("reports"),
    USERS("users"),
    USER_TYPES("user-types"),
    COMPANIES("companies"),
    PAYMENT_MODES("payment-modes"),
    PLAN_TYPES("plan-types"),
    NATIONALITIES("nationalities"),
    ARRIVAL_MODES("arrival-modes"),
    REF_MODES("ref-modes"),
    RESERVATION_SOURCES("reservation-sources"),
    TAXES("taxes"),
    ACCOUNT_HEADS("account-heads"),
    OPERATIONS("operations"),
    HOUSEKEEPING("housekeeping"),
    STAFF_MANAGEMENT("staff"),
    ADMIN("admin");
    
    private final String moduleName;
    
    ModuleName(String moduleName) {
        this.moduleName = moduleName;
    }
    
    public String getModuleName() {
        return moduleName;
    }
    
    public static ModuleName fromString(String moduleName) {
        for (ModuleName module : values()) {
            if (module.getModuleName().equals(moduleName)) {
                return module;
            }
        }
        throw new IllegalArgumentException("Unknown module name: " + moduleName);
    }
}