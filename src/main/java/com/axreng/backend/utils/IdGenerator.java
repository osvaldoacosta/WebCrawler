package com.axreng.backend.utils;

import java.util.UUID;

public class IdGenerator {
    public static String generateId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}
