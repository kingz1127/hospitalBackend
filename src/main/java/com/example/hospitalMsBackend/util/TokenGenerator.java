package com.example.hospitalMsBackend.util;

import java.util.concurrent.ThreadLocalRandom;

public class TokenGenerator {
    public static String generatePatientToken() {
        int randomNum = ThreadLocalRandom.current().nextInt(10000, 99999 + 1);
        return "HOS-" + randomNum;
    }
}
