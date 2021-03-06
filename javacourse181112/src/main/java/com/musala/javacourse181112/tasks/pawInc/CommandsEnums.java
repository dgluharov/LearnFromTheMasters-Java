package com.musala.javacourse181112.tasks.pawInc;

import java.util.Arrays;

public enum CommandsEnums {
    YES("Yes", 1),
    NO("No", 1),
    REGISTER_CLEANSING_CENTER("RegisterCleansingCenter", 1),
    REGISTER_ADOPTION_CENTER("RegisterAdoptionCenter", 1),
    REGISTER_DOG("RegisterDog", 1),
    REGISTER_CAT("RegisterCat", 1),
    SEND_FOR_CLEANSING("SendForCleansing", 1),
    CLEANSE("Cleanse", 1),
    ADOPT("Adopt", 1),
    PRINT_STATISTICS("PrintStatistics", 1),
    PAW_PAW_PAWAH("End", 1);


    private String label;
    private int order;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    CommandsEnums(final String label, final int order) {
        this.label = label;
        this.order = order;
    }

    @Override
    public String toString() {
        return name() + " " + label + " " + order;
    }

    public static CommandsEnums fromString(final String label) {
        return Arrays.stream(values())
                .filter(extendedEnumSample -> extendedEnumSample.getLabel().equals(label))
                .findAny()
                .orElseThrow(() -> new RuntimeException("not found"));
    }
}
