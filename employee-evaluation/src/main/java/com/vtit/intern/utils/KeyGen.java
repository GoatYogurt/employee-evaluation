package com.vtit.intern.utils;

import java.security.SecureRandom;
import java.util.Base64;

public class KeyGen {
    public static void main(String[] args) {
        SecureRandom random = new SecureRandom();
        byte[] key = new byte[64];
        random.nextBytes(key);
        System.out.println(Base64.getEncoder().encodeToString(key));
    }
}
