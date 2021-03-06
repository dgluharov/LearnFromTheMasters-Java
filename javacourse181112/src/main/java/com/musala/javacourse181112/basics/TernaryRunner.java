package com.musala.javacourse181112.basics;

/**
 * Created by Iva Koleva on 19.11.2018
 */
public class TernaryRunner {
    public static void main(final String[] args) {
        String value = "yes";

        int yesCounter = 0;
        int noCounter = 0;

        if ("yes".equals(value)) {
            yesCounter++;
        } else if ("no".equals(value)) {
            yesCounter++;
        }

        // TODO: 2-lines implementation, using ternary operator

        yesCounter += ("yes".equals(value)) ? 1 : 0;
        noCounter += ("no".equals(value)) ? 1 : 0;
    }
}
