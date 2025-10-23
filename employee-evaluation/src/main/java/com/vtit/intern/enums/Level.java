package com.vtit.intern.enums;

public enum Level {
    FRESHER, JUNIOR, JUNIOR_PLUS, MIDDLE, MIDDLE_PLUS, SENIOR;

    public static boolean contains(String test) {
        for (Level l : Level.values()) {
            if (l.name().equals(test)) {
                return true;
            }
        }
        return false;
    }

    public String toDisplayString() {
        return this.name()
                .toLowerCase()
                .replace("_plus", "+");
    }
}
