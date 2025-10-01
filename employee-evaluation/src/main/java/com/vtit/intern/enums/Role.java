package com.vtit.intern.enums;

public enum Role {
    PGDTT, ADMIN, PM, DEV, TESTER, BA, UIUX, AI, DATA, QA, VHKT, MKT;

    public static boolean contains(String test) {
        for (Role r : Role.values()) {
            if (r.name().equals(test)) {
                return true;
            }
        }
        return false;
    }
}
