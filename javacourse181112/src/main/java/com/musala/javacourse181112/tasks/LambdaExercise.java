package com.musala.javacourse181112.tasks;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Created by Iva Koleva on 03.12.2018
 */

/* Lambda exercise
 *** implement String to StringBuilder function
 *** implement String predicate - if string is length of 10*/

public class LambdaExercise {
    public static void main(final String[] args) {
        final Function<String, StringBuilder> stringStringBuilderFunction = (i) -> new StringBuilder(i);
        final Predicate<String> lengthString = a -> a.length() == 10;


    }
}
