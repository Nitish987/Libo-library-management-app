package com.nk.libo.utils;

import java.util.Random;

public class Utility {
    public Utility() {}

    public static String getRandomString(int size) {
        Random random = new Random();
        String alphaNumeric = "abcdefghijklmnopqrstuwxyz_01234567890";
        int length = alphaNumeric.length();
        StringBuilder builder = new StringBuilder("");
        for (int i = 0; i < size; i++) {
            builder.append(alphaNumeric.charAt(random.nextInt(length)));
        }
        return builder.toString();
    }
}
