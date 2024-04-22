package ch.uzh.ifi.hase.soprafs24.util;

import java.util.UUID;

public class FormIdGenerator {

    public FormIdGenerator(){

    }
    public static String generateFormId() {
        return UUID.randomUUID().toString();
    }
}