package com.darmokhval.utility;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * utility class, responsible for user's input validation (path to files and field to search)
 */
public class ArgsValidator {

    private static final Set<String> VALID_FIELDS = new HashSet<>();
    static {
        VALID_FIELDS.add("brand");
        VALID_FIELDS.add("model");
        VALID_FIELDS.add("year_of_release");
        VALID_FIELDS.add("owner");
        VALID_FIELDS.add("mileage");
        VALID_FIELDS.add("accessories");
        VALID_FIELDS.add("was_in_accident");
    }

    public void checkForCorrectArgs(String ...args){
        if(args == null || args.length != 2) {
            throw new IllegalArgumentException("Number of arguments are not supported");
        }

        String directoryPath = args[0];
        String fieldAttribute = args[1];

        File directory = new File(directoryPath);
        if (!directory.exists() || !directory.isDirectory()) {
            throw new IllegalArgumentException("Invalid directory path.");
        }
        File[] files = directory.listFiles();
        if(files == null || files.length == 0) {
            throw new IllegalArgumentException("There are no files inside of a specified directory");
        }
        if(fieldAttribute.isBlank()) {
            throw new IllegalArgumentException("Field attribute can't be blank");
        }
        if(!isValidField(fieldAttribute)) {
            throw new IllegalArgumentException("Field attribute not supported");
        }
    }

    private boolean isValidField(String attribute) {
        return VALID_FIELDS.contains(attribute);
    }
}
